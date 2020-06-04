package org.gvm.product.gvmpoin.module.frauddetection;

import org.gvm.product.gvmpoin.module.campaign.leaderboard.Leaderboard;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.LeaderboardTransactionLog;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.LeaderboardTransactionLogRepository;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntry;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by sofian-hadianto on 5/4/17.
 */
@Service
@PropertySource("fraud-detection.properties")
public class FraudDetectionService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private static final String COUNT_HIT_KEY = "COUNT_HIT_FOR-";
  private static final String BLOCKED_REQ_KEY = "BLOCK_REQ-";
  private static final String LIST_ROLLBACK_JOURNAL_ENTRY = "LIST_ROLLBACK_JOURNAL_ENTRY-";
  private static final String LIST_ROLLBACK_LEADERBOARD_TRANSACTION_LOG =
      "LIST_ROLLBACK_LEADERBOARD_TRANSACTION_LOG-";
  private static final String PARAM_PSID = "ps_id";
  private static final String ERROR_MSG = "Your request has been rejected "
      + " due to suspicious activity";

  @Autowired
  private FraudDataRedisRepository fraudDataRedisRepository;

  @Autowired
  private JournalEntryRepository journalEntryRepository;

  @Autowired
  private LeaderboardTransactionLogRepository leaderboardTransactionLogRepository;

  @Value("${fraud.count.timetolive}")
  private long countObjectTimeToLive;

  @Value("${fraud.count.maximum}")
  private long maximumHit;

  @Value("${fraud.blocked.duration}")
  private long blockedDuration;

  @Value("${fraud.list.rollback.timetolive}")
  private long listRollbackObjectTimeToLive;

  void filteringIncomingPostPutRequest(HttpServletRequest request) {
    if (isPostPutRequest(request) && isContainAuthHeader(request)) {
      String psId = request.getParameter(PARAM_PSID);

      log.debug("checking whether request from ps_id " + psId + " is currently blocked");

      if (isBlocked(psId)) {
        // make flag in journal entry and trans log table
        makeFraudTransactionFlagInDb(psId);

        // force delete list journal entry and trans log in redis
        deleteListRollbackJournalEntry(psId);
        deleteListRollbackLeaderboardTransactionLog(psId);

        throw new AccessDeniedException(ERROR_MSG);
      }

      log.debug("checking whether ps_id " + psId + " have counting data in redis server");

      if (isCurrentCountExist(psId)) {
        incrementCount(psId);
      } else {
        saveNewCount(psId);
        setCountObjectExpiration(psId, countObjectTimeToLive);
      }

      long currentCount = getCurrentCount(psId);
      if (currentCount >= maximumHit) {
        log.debug("ps_id " + psId + " is eligible to be blocked since its counting "
            + currentCount);
        setBlockedStatus(psId);
        setBlockedDuration(psId, blockedDuration);
      }

    }
  }

  void filteringOutcomePostPutResponse(ServerHttpRequest request, Object responseBody) {
    if (responseBody instanceof Response && isPostPutRequest(request)) {
      Response respBody = (Response) responseBody;
      Object data = respBody.getData();

      log.debug("trying to catch response and check response type before its written..........");

      String psId;
      if (data instanceof Leaderboard) {
        log.debug("this body message instanceof leaderboard object ............");
        Leaderboard leaderboard = (Leaderboard) data;
        psId = leaderboard.getPsId();
        long leaderboardTransactionLogId = leaderboard.getLeaderboardTransactionLog().getId();

        addLeaderboardTransactionLogNeedToBeBlocked(psId, leaderboardTransactionLogId);
      } else if (data instanceof JournalEntry) {
        log.debug("this body message instanceof journal entry object ............");
        JournalEntry journalEntry = (JournalEntry) data;
        psId = journalEntry.getTrialBalance().getOwner().getPsId();
        long journalEntryId = journalEntry.getId();

        addJournalEntryNeedToBeBlocked(psId, journalEntryId);
      }
    }
  }

  private void saveNewCount(String psId) {
    final int initialValue = 1;
    fraudDataRedisRepository.save(COUNT_HIT_KEY + psId, initialValue);
  }

  private void setBlockedStatus(String psId) {
    fraudDataRedisRepository.save(BLOCKED_REQ_KEY + psId, true);
  }

  private void setBlockedDuration(String psId, long timeoutInSeconds) {
    fraudDataRedisRepository.expire(BLOCKED_REQ_KEY + psId, timeoutInSeconds);
  }

  private void setCountObjectExpiration(String psId, long timeoutInSeconds) {
    fraudDataRedisRepository.expire(COUNT_HIT_KEY + psId, timeoutInSeconds);
  }

  private long incrementCount(String psId) {
    final long deltaEachIncrement = 1L;
    return fraudDataRedisRepository.increment(COUNT_HIT_KEY + psId, deltaEachIncrement);
  }

  private int getCurrentCount(String psId) {
    Object object = fraudDataRedisRepository.findOneByKey(COUNT_HIT_KEY + psId);

    int result = 0;
    if (object != null) {
      result = Integer.parseInt(object.toString());
    }

    return result;
  }

  private boolean isCurrentCountExist(String psId) {
    Object object = fraudDataRedisRepository.findOneByKey(COUNT_HIT_KEY + psId);
    return object != null;
  }

  private boolean isBlocked(String psId) {
    Object object = fraudDataRedisRepository.findOneByKey(BLOCKED_REQ_KEY + psId);
    return object != null;
  }

  private void addJournalEntryNeedToBeBlocked(String psId, long journalEntryId) {
    if (isListRollbackJournalEntryExist(psId)) {
      fraudDataRedisRepository.saveToList(LIST_ROLLBACK_JOURNAL_ENTRY + psId, journalEntryId);
    } else {
      // these two must run in one hit
      fraudDataRedisRepository.saveToList(LIST_ROLLBACK_JOURNAL_ENTRY + psId, journalEntryId);
      fraudDataRedisRepository.expire(LIST_ROLLBACK_JOURNAL_ENTRY + psId,
          listRollbackObjectTimeToLive);
    }

  }

  private void addLeaderboardTransactionLogNeedToBeBlocked(String psId,
      long leaderboardTransactionLogId) {
    if (isListRollbackLeaderboardTransactionLogExist(psId)) {
      fraudDataRedisRepository.saveToList(LIST_ROLLBACK_LEADERBOARD_TRANSACTION_LOG + psId,
          leaderboardTransactionLogId);
    } else {
      // these two must run in one hit
      fraudDataRedisRepository.saveToList(LIST_ROLLBACK_LEADERBOARD_TRANSACTION_LOG + psId,
          leaderboardTransactionLogId);
      fraudDataRedisRepository.expire(LIST_ROLLBACK_LEADERBOARD_TRANSACTION_LOG + psId,
          listRollbackObjectTimeToLive);
    }
  }

  private void deleteListRollbackJournalEntry(String psId) {
    fraudDataRedisRepository.delete(LIST_ROLLBACK_JOURNAL_ENTRY + psId);
  }

  private void deleteListRollbackLeaderboardTransactionLog(String psId) {
    fraudDataRedisRepository.delete(LIST_ROLLBACK_LEADERBOARD_TRANSACTION_LOG + psId);
  }

  private boolean isListRollbackJournalEntryExist(String psId) {
    long size = fraudDataRedisRepository.listSize(LIST_ROLLBACK_JOURNAL_ENTRY + psId);
    return size > 0;
  }

  private boolean isListRollbackLeaderboardTransactionLogExist(String psId) {
    long size = fraudDataRedisRepository
        .listSize(LIST_ROLLBACK_LEADERBOARD_TRANSACTION_LOG + psId);
    return size > 0;
  }

  private boolean isContainAuthHeader(HttpServletRequest request) {
    final String headerAuthorization = "Authorization";
    String authHeader =
        (request.getHeader(headerAuthorization) != null) ? request.getHeader(headerAuthorization)
            : "";

    return !authHeader.equals("");
  }

  private boolean isPostPutRequest(HttpServletRequest request) {
    String requestMethod = request.getMethod().toLowerCase();

    return requestMethod.equals("post") || requestMethod.equals("put");
  }

  private boolean isPostPutRequest(ServerHttpRequest request) {
    HttpMethod requestMethod = request.getMethod();

    return requestMethod.equals(HttpMethod.POST) || requestMethod.equals(HttpMethod.PUT);
  }

  @Transactional
  private void makeFraudTransactionFlagInDb(String psId) {
    // get list value
    final long start = 0L;
    final long end = 5L;
    List<String> elementsRollbackJournalEntry = fraudDataRedisRepository
        .findAllListElement(LIST_ROLLBACK_JOURNAL_ENTRY + psId, start, end);

    long id;
    Optional<JournalEntry> optJournalEntry;
    JournalEntry journalEntry;
    for (String element : elementsRollbackJournalEntry) {
      id = Long.parseLong(element);
      optJournalEntry = journalEntryRepository.findOneById(id);
      if (optJournalEntry.isPresent()) {
        journalEntry = optJournalEntry.get();
        journalEntry.setFraudTransaction(true);
        journalEntryRepository.saveAndFlush(journalEntry);
      }
    }

    List<String> elementsRollbackTransactionLog = fraudDataRedisRepository
        .findAllListElement(LIST_ROLLBACK_LEADERBOARD_TRANSACTION_LOG + psId, start, end);
    LeaderboardTransactionLog leaderboardTransactionLog;
    Optional<LeaderboardTransactionLog> optLeaderboardTransactionLog;
    for (String element : elementsRollbackTransactionLog) {
      id = Long.parseLong(element);
      optLeaderboardTransactionLog = leaderboardTransactionLogRepository.findOneById(id);

      if (optLeaderboardTransactionLog.isPresent()) {
        leaderboardTransactionLog = optLeaderboardTransactionLog.get();
        leaderboardTransactionLog.setFraudTransaction(true);
        leaderboardTransactionLogRepository.saveAndFlush(leaderboardTransactionLog);
      }
    }
  }
}
