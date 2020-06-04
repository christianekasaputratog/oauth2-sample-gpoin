package org.gvm.product.gvmpoin.module.trialbalance.integration;

import java.util.Objects;
import org.apache.commons.codec.binary.Base64;
import org.gvm.product.gvmpoin.module.campaign.CampaignService;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.Leaderboard;
import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.client.ClientConsumerRepository;
import org.gvm.product.gvmpoin.module.client.ClientRepository;
import org.gvm.product.gvmpoin.module.client.exception.ClientNotFoundException;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.JsonData;
import org.gvm.product.gvmpoin.module.common.RollbackTransaction;
import org.gvm.product.gvmpoin.module.common.WrapperJson;
import org.gvm.product.gvmpoin.module.common.exception.ExceededBalanceException;
import org.gvm.product.gvmpoin.module.common.exception.HashNotValidException;
import org.gvm.product.gvmpoin.module.common.exception.NegativeNumberException;
import org.gvm.product.gvmpoin.module.common.exception.PsIdNotFoundException;
import org.gvm.product.gvmpoin.module.common.exception.PsIdTemporarySuspendedException;
import org.gvm.product.gvmpoin.module.common.exception.TransactionIdNotFoundException;
import org.gvm.product.gvmpoin.module.consumer.ClientConsumer;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.consumer.client.ConsumerClient;
import org.gvm.product.gvmpoin.module.consumer.client.ConsumerClientRepository;
import org.gvm.product.gvmpoin.module.integrator.AuthToken;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntry;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntryRepository;
import org.gvm.product.gvmpoin.module.tiersystem.TierSystemService;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalance;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceNotFoundException;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceParam;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceRepository;
import org.gvm.product.gvmpoin.util.EmailBlaster;
import org.gvm.product.gvmpoin.util.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

@Service
public class TrialBalanceIntegrationService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private static final String DESCRIPTION_ADD_POINT = "ADD_POINT_REGULAR";
  private static final String DESCRIPTION_SUBSTRACT_POINT = "SUBSTRACT_POINT_REGULAR";
  private static final String CATEGORY_REGULAR_TRANSACTION = "reguler";
  private static final String CATEGORY_LEADERBOARD_TRANSACTION = "campaign";
  private static final String DESCRIPTION_ROLLBACK =
      "ROLLBACK_TRANSACTION-%s-WITH-TRANSACTIONID-%s";
  private static final String ROLLBACK_ACTIVITY = "ROLLBACK";
  private static final String ROLLBACK_ACTIVITY_OBJECT_LEADERBOARD = "LEADERBOARD_TRANSACTION_LOG";
  private static final String ROLLBACK_ACTIVITY_OBJECT_JOURNAL_ENTRY = "JOURNAL_ENTRY";
  private static final String DESCRIPTION_CLIENT_CAP = "REACH_MAX_LIMIT_OF_DAILY_POINTS_CAP";
  private static final String TRIVIA_ACTIVITY_OBJECT = "TRIVIA";

  private TrialBalanceRepository trialBalanceRepository;
  private JournalEntryRepository journalEntryRepository;
  private ConsumerRepository consumerRepository;
  private Md5PasswordEncoder md5PasswordEncoder;
  private ClientRepository clientRepository;
  private CampaignService campaignService;
  private TierSystemService tierSystemService;
  private ClientConsumerRepository clientConsumerRepository;
  private ConsumerClientRepository consumerClientRepository;
  private EmailBlaster emailBlaster;

  @Value("${womantalk.auth}")
  private String womanTalkAuthUrl;
  @Value("${womantalk.api.url}")
  private String womantalkApiUrl;
  @Value("${womantalk.access_key}")
  private String womantalkAccessKey;
  @Value("${womantalk.secret_key}")
  private String womantalkSecretKey;

  /**
   * Bean Configuration for Trial Balance Backend Integration .
   *
   * @param trialBalanceRepository Trial Balance Interface
   * @param journalEntryRepository Journal Entry Interface
   * @param consumerRepository Consumer Interface
   * @param clientRepository Client Interface
   * @param md5PasswordEncoder MD5 Password Interface
   * @param campaignService Campaign Implementation
   * @param tierSystemService Tier System Implementation
   * @param clientConsumerRepository Client Consumer Interface
   * @param consumerClientRepository Consumer Client Interface
   * @param emailBlaster Email Blaster Global Function Class
   */
  @Autowired
  public TrialBalanceIntegrationService(
      TrialBalanceRepository trialBalanceRepository, JournalEntryRepository journalEntryRepository,
      ConsumerRepository consumerRepository, ClientRepository clientRepository,
      Md5PasswordEncoder md5PasswordEncoder, CampaignService campaignService,
      TierSystemService tierSystemService, ClientConsumerRepository clientConsumerRepository,
      ConsumerClientRepository consumerClientRepository, EmailBlaster emailBlaster) {
    this.trialBalanceRepository = trialBalanceRepository;
    this.journalEntryRepository = journalEntryRepository;
    this.consumerRepository = consumerRepository;
    this.md5PasswordEncoder = md5PasswordEncoder;
    this.clientRepository = clientRepository;
    this.campaignService = campaignService;
    this.tierSystemService = tierSystemService;
    this.clientConsumerRepository = clientConsumerRepository;
    this.consumerClientRepository = consumerClientRepository;
    this.emailBlaster = emailBlaster;
  }

  /**
   * Add Balance .
   *
   * @param addBalanceParam Builder Param for Trial Balance
   * @return Detail of Journal Entry
   */
  @Transactional
  public JournalEntry credit(TrialBalanceParam addBalanceParam) {
    Client client = clientRepository.findOneByClientId(addBalanceParam.getClientId())
        .orElseThrow(() -> new ClientNotFoundException(addBalanceParam.getClientId()));

    if (addBalanceParam.getAmount() < 0) {
      throw new NegativeNumberException();
    }

    Optional<Consumer> checkConsumer = consumerRepository.findOneByPsId(addBalanceParam.getPsId());

    JournalEntry journalEntry = new JournalEntry();
    if (checkConsumer.isPresent()) {
      checkExistConsumerClient(checkConsumer.get(), client);
      Consumer consumer = checkConsumer.get();
      boolean matchHash = md5PasswordEncoder.isPasswordValid(addBalanceParam.getHash(),
          consumer.getPsId(), Constant.SALT);

      if (consumer.getStatus().equals(Constant.FLAG_ACTIVE_PSID)) {
        if (matchHash) {
          Optional<TrialBalance> checkBalance = trialBalanceRepository
              .findTrialBalanceForUpdate(consumer.getTrialBalance().getId());

          if (checkBalance.isPresent()) {
            TrialBalance trialBalance = checkBalance.get();
            addBalanceParam.setDescription(DESCRIPTION_ADD_POINT);
            Optional<JournalEntry> optJournalEntry = this
                .createJournalEntryForCredit(trialBalance, addBalanceParam, checkConsumer.get(),
                    client);
            if (optJournalEntry.isPresent()) {
              journalEntry = optJournalEntry.get();
              tierSystemService.addLevelPointWhenTrialBalanceCredit(addBalanceParam);
            }

            return journalEntry;

          } else {
            throw new TrialBalanceNotFoundException();
          }

        } else {
          throw new HashNotValidException(addBalanceParam.getHash());
        }

      } else {
        throw new PsIdTemporarySuspendedException(addBalanceParam.getPsId());
      }

    } else {
      throw new PsIdNotFoundException(addBalanceParam.getPsId());
    }

  }

  private void checkExistConsumerClient(Consumer consumer, Client client) {
    Optional<ConsumerClient> consumerClient = consumerClientRepository
        .findOneByPsIdAndClientId(consumer.getPsId(), client.getClientId());
    if(!consumerClient.isPresent()) {
      buildNewConsumerClient(consumer, client);
    }
  }

  private void buildNewConsumerClient(Consumer consumer, Client client) {

    ConsumerClient consumerClient = new ConsumerClient();
    consumerClient.setConsumer(consumer);
    consumerClient.setClient(client);

    consumerClientRepository.saveAndFlush(consumerClient);
  }

  private Optional<JournalEntry> createJournalEntryForCredit(TrialBalance trialBalance,
      TrialBalanceParam addBalanceParam, Consumer consumer, Client client) {
    Integer lastOpeningBalance = trialBalance.getOpeningBalance();
    Integer lastTotalDebits = trialBalance.getTotalDebits();
    Integer lastTotalCredits = trialBalance.getTotalCredits();
    Integer consumerTotalCreditToday = journalEntryRepository
        .sumConsumerCreditTodayExcludeActivityObject(consumer.getPsId(), TRIVIA_ACTIVITY_OBJECT);

    if (consumerTotalCreditToday == null) {
      consumerTotalCreditToday = 0;
    }
    Integer finalPointAdded = consumerTotalCreditToday + addBalanceParam.getAmount();
    Integer dailyPointCap = client.getDailyPointCap();

    Integer nextTotalCredits = lastTotalCredits + setAmountBasedDailyPointCapInClient(
        addBalanceParam, consumerTotalCreditToday, dailyPointCap, finalPointAdded);
    Integer nextClosingBalance = lastOpeningBalance + nextTotalCredits - lastTotalDebits;

    trialBalance.setOpeningBalance(lastOpeningBalance);
    trialBalance.setTotalDebits(lastTotalDebits);
    trialBalance.setTotalCredits(nextTotalCredits);
    trialBalance.setClosingBalance(nextClosingBalance);

    JournalEntry journalEntry = new JournalEntry();

    if (addBalanceParam.getAmount() > 0) {
      journalEntry.setClientId(addBalanceParam.getClientId());

      journalEntry.setDebit(0);
      journalEntry.setDescription(addBalanceParam.getDescription());
      journalEntry.setBalance(nextClosingBalance);
      setConsumerPointBasedDailyPointCapInClient(journalEntry, addBalanceParam,
          consumerTotalCreditToday, dailyPointCap, finalPointAdded);
      journalEntry.setTrialBalance(trialBalance);
      journalEntry.setActivity(addBalanceParam.getActivity());
      journalEntry.setActivityObject(addBalanceParam.getActivityObject());
      journalEntry.setObjectId(addBalanceParam.getObjectId());
      journalEntry.setAdditionalData(addBalanceParam.getAdditionalData());
      journalEntry.setClientTransactionId(addBalanceParam.getClientTransactionId());
      journalEntry.setConsumer(consumer);

      journalEntry = journalEntryRepository.saveAndFlush(journalEntry);
    }

    return Optional.ofNullable(journalEntry);
  }

  private Integer setAmountBasedDailyPointCapInClient(TrialBalanceParam addBalanceParam,
      Integer consumerTotalCreditToday, Integer dailyPointCap,
      Integer finalPointAdded) {

    if (consumerTotalCreditToday == null) {
      consumerTotalCreditToday = 0;
    }
    if (dailyPointCap > 0 && (consumerTotalCreditToday >= dailyPointCap
        || finalPointAdded > dailyPointCap || addBalanceParam.getAmount() > dailyPointCap)
        && !Objects.equals(addBalanceParam.getActivityObject(), TRIVIA_ACTIVITY_OBJECT)) {
      return 0;
    }
    return addBalanceParam.getAmount();
  }

  private void setConsumerPointBasedDailyPointCapInClient(JournalEntry journalEntry,
      TrialBalanceParam addBalanceParam,
      Integer consumerTotalCreditToday, Integer dailyPointCap, Integer finalPointAdded) {

    if (consumerTotalCreditToday == null) {
      consumerTotalCreditToday = 0;
    }
    if (dailyPointCap > 0 && (consumerTotalCreditToday >= dailyPointCap
        || finalPointAdded > dailyPointCap || addBalanceParam.getAmount() > dailyPointCap)
        && !Objects.equals(addBalanceParam.getActivityObject(), TRIVIA_ACTIVITY_OBJECT)) {
      journalEntry.setCredit(0);
      journalEntry.setDescription(DESCRIPTION_CLIENT_CAP);
    } else {
      journalEntry.setCredit(addBalanceParam.getAmount());
    }
  }

  /**
   * Substract Balance .
   *
   * @param substractBalanceParam Builder Param for Trial Balance
   * @return Detail of Journal Entry
   */
  @Transactional
  public JournalEntry debit(TrialBalanceParam substractBalanceParam) {

    if (substractBalanceParam.getAmount() < 0) {
      throw new NegativeNumberException();
    }

    clientRepository.findOneByClientId(substractBalanceParam.getClientId())
        .orElseThrow(() -> new ClientNotFoundException(substractBalanceParam.getClientId()));

    Optional<Consumer> checkConsumer = consumerRepository
        .findOneByPsId(substractBalanceParam.getPsId());

    JournalEntry journalEntry = new JournalEntry();
    if (checkConsumer.isPresent()) {
      Consumer consumer = checkConsumer.get();
      boolean matchHash = md5PasswordEncoder.isPasswordValid(substractBalanceParam.getHash(),
          consumer.getPsId(), Constant.SALT);

      if (consumer.getStatus().equals(Constant.FLAG_ACTIVE_PSID)) {
        if (matchHash) {
          Optional<TrialBalance> checkBalance =
              trialBalanceRepository.findTrialBalanceForUpdate(consumer.getTrialBalance().getId());

          if (checkBalance.isPresent()) {
            TrialBalance trialBalance = checkBalance.get();

            if (substractBalanceParam.getAmount() <= trialBalance.getClosingBalance()) {
              substractBalanceParam.setDescription(DESCRIPTION_SUBSTRACT_POINT);
              Optional<JournalEntry> optJournalEntry = this.createJournalEntryForDebit(trialBalance,
                  substractBalanceParam, checkConsumer.get());

              if (optJournalEntry.isPresent()) {
                journalEntry = optJournalEntry.get();
              }

            } else {
              throw new ExceededBalanceException();
            }

            return journalEntry;

          } else {
            throw new TrialBalanceNotFoundException();
          }

        } else {
          throw new HashNotValidException(substractBalanceParam.getHash());
        }

      } else {
        throw new PsIdTemporarySuspendedException(substractBalanceParam.getPsId());
      }

    } else {
      throw new PsIdNotFoundException(substractBalanceParam.getPsId());
    }
  }

  private Optional<JournalEntry> createJournalEntryForDebit(TrialBalance trialBalance,
      TrialBalanceParam substractBalanceParam, Consumer consumer) {
    Integer lastOpeningBalance = trialBalance.getOpeningBalance();
    Integer lastTotalDebits = trialBalance.getTotalDebits();
    Integer lastTotalCredits = trialBalance.getTotalCredits();

    Integer nextTotalDebits = lastTotalDebits + substractBalanceParam.getAmount();
    Integer nextClosingBalance = lastOpeningBalance + lastTotalCredits - nextTotalDebits;

    trialBalance.setOpeningBalance(lastOpeningBalance);
    trialBalance.setTotalDebits(nextTotalDebits);
    trialBalance.setTotalCredits(lastTotalCredits);
    trialBalance.setClosingBalance(nextClosingBalance);

    JournalEntry journalEntry = new JournalEntry();

    if (nextClosingBalance >= 0) {
      journalEntry.setClientId(substractBalanceParam.getClientId());
      journalEntry.setDebit(substractBalanceParam.getAmount());
      journalEntry.setCredit(0);
      journalEntry.setDescription(substractBalanceParam.getDescription());
      journalEntry.setTrialBalance(trialBalance);
      journalEntry.setBalance(nextClosingBalance);
      journalEntry.setActivity(substractBalanceParam.getActivity());
      journalEntry.setActivityObject(substractBalanceParam.getActivityObject());
      journalEntry.setObjectId(substractBalanceParam.getObjectId());
      journalEntry.setAdditionalData(substractBalanceParam.getAdditionalData());
      journalEntry.setClientTransactionId(substractBalanceParam.getClientTransactionId());
      journalEntry.setConsumer(consumer);

      if (substractBalanceParam.getAmount() > 0) {
        journalEntry = journalEntryRepository.saveAndFlush(journalEntry);
      }
    }

    return Optional.ofNullable(journalEntry);
  }

  /**
   * Get Trial Balance by PS ID .
   *
   * @param psId GPoin Unique ID
   * @param hash Hashed PS ID
   * @return Detail of Trial Balance
   */
  public TrialBalance getTrialBalance(String psId, String hash) {
    Optional<Consumer> checkConsumer = consumerRepository.findOneByPsId(psId);

    TrialBalance trialBalance;
    if (checkConsumer.isPresent()) {
      Consumer consumer = checkConsumer.get();
      boolean matchHash = md5PasswordEncoder.isPasswordValid(hash, consumer.getPsId(),
          Constant.SALT);

      trialBalance = returnTrialBalanceWhenHashMatched(hash, consumer, matchHash);

    } else {
      throw new PsIdNotFoundException(psId);
    }

    return trialBalance;
  }

  private TrialBalance returnTrialBalanceWhenHashMatched(String hash, Consumer consumer,
      boolean matchHash) {
    TrialBalance trialBalance;
    if (matchHash) {
      trialBalance = consumer.getTrialBalance();
    } else {
      throw new HashNotValidException(hash);
    }
    return trialBalance;
  }

  /**
   * Get Transaction History by PS ID .
   *
   * @param pageNumber Equals to OFFSET Query
   * @param psId GPoin Unique ID
   * @param hash Hashed PS ID
   * @return List of Journal Entry
   */
  public List<JournalEntry> getTransactionHistory(int pageNumber, String psId,
      String hash) {
    Optional<Consumer> checkConsumer = consumerRepository.findOneByPsId(psId);

    if (checkConsumer.isPresent()) {
      Consumer consumer = checkConsumer.get();
      boolean matchHash = md5PasswordEncoder.isPasswordValid(hash, consumer.getPsId(),
          Constant.SALT);

      if (matchHash) {
        PageRequest pageRequest =
            new PageRequest(pageNumber - 1, Constant.PAGE_SIZE, Sort.Direction.DESC,
                "id");
        Page<JournalEntry> data = journalEntryRepository
            .findByTrialBalanceId(consumer.getTrialBalance().getId(), pageRequest);
        return data.getContent();
      } else {
        throw new HashNotValidException(hash);
      }
    }
    throw new PsIdNotFoundException(psId);
  }

  public JournalEntry getJournalEntryBasedOnTransactionId(Long transactionId) {
    return journalEntryRepository.findOneById(transactionId)
        .orElseThrow(() -> new TransactionIdNotFoundException(transactionId));
  }

  /**
   * Rollback Transaction .
   *
   * @param clientTransactionId Transaction ID
   * @param reason Reason due to rollback request
   * @return Detail of Rollback Transaction
   */
  @Transactional
  public RollbackTransaction rollbackTransaction(String clientTransactionId, String reason) {
    JournalEntry rollbackJournalEntry = new JournalEntry();
    String psId;
    String hash;
    Leaderboard rollbackLeaderboard = new Leaderboard();

    if (!this.areYouHaveSentRollbackJournalEntry(clientTransactionId)) {
      Optional<JournalEntry> optJournalEntry = journalEntryRepository
          .findOneByClientTransactionId(clientTransactionId);
      if (optJournalEntry.isPresent()) {
        JournalEntry currentJournalEntry = optJournalEntry.get();
        TrialBalance trialBalance = currentJournalEntry.getTrialBalance();
        Consumer consumer = consumerRepository.findOneByTrialBalanceId(trialBalance.getId());

        psId = consumer.getPsId();
        hash = HashUtil.getHash(psId);
        rollbackJournalEntry = this.createJournalEntryForRollback(psId, hash, currentJournalEntry,
            reason);
      }
    }

//    if (!campaignService.areYouHaveSentRollbackLeaderboardTransactionLog(clientTransactionId)) {
//      Optional<LeaderboardTransactionLog> optLeaderboardTransactionLog = campaignService
//          .getLeaderboardTransactionLogByClientTransactionId(clientTransactionId);
//      if (optLeaderboardTransactionLog.isPresent()) {
//        LeaderboardTransactionLog currentLeaderboardTransactionLog =
//            optLeaderboardTransactionLog.get();
//        Leaderboard currentleaderboard = campaignService
//            .getLeaderboardById(currentLeaderboardTransactionLog.getLeaderboardId());
//
//        psId = currentleaderboard.getPsId();
//        hash = HashUtil.getHash(psId);
//        rollbackLeaderboard = this.createLeaderboardForRollback(psId, hash,
//            currentLeaderboardTransactionLog, reason);
//      }
//    }

    return new RollbackTransaction(rollbackJournalEntry, rollbackLeaderboard);
  }

  private boolean areYouHaveSentRollbackJournalEntry(String clientTransactionId) {
    Long count = journalEntryRepository.countRollbackTransaction(clientTransactionId);

    return count > 0;
  }

  private JournalEntry createJournalEntryForRollback(String psId, String hash,
      JournalEntry journalNeedToRollback, String reason) {
    int amount = 0;
    JournalEntry journalAfterRollback = new JournalEntry();

    logger.info("rollback transaction regular point.........");
    String description = String.format(DESCRIPTION_ROLLBACK, CATEGORY_REGULAR_TRANSACTION,
        journalNeedToRollback.getId());

    if (journalNeedToRollback.getDebit() > 0) {
      amount = journalNeedToRollback.getDebit();
    } else if (journalNeedToRollback.getCredit() > 0) {
      amount = journalNeedToRollback.getCredit();
    }

    TrialBalanceParam.Builder builder = new TrialBalanceParam.Builder(psId, hash, amount,
        ROLLBACK_ACTIVITY, ROLLBACK_ACTIVITY_OBJECT_JOURNAL_ENTRY,
        journalNeedToRollback.getClientId());

    final TrialBalanceParam paramRollback = builder.description(description)
        .objectId(journalNeedToRollback.getId())
        .clientTransactionId(journalNeedToRollback.getClientTransactionId())
        .additionalData(reason)
        .build();

    if (journalNeedToRollback.getDebit() > 0) {
      journalAfterRollback = this.credit(paramRollback);
    } else if (journalNeedToRollback.getCredit() > 0) {
      journalAfterRollback = this.debit(paramRollback);
    }

    return journalAfterRollback;
  }

//  private Leaderboard createLeaderboardForRollback(String psId, String hash,
//      LeaderboardTransactionLog leaderboardTransactionLogNeedToRollback,
//      String reason) {
//    Leaderboard leaderboardAfterRollback = new Leaderboard();
//    logger.info("rollback transaction leaderboard/campaign point.........");
//    String description = String.format(DESCRIPTION_ROLLBACK, CATEGORY_LEADERBOARD_TRANSACTION,
//        leaderboardTransactionLogNeedToRollback.getId());
//
//    int amount = 0;
//    if (leaderboardTransactionLogNeedToRollback.getDebit() > 0) {
//      amount = leaderboardTransactionLogNeedToRollback.getDebit();
//    } else if (leaderboardTransactionLogNeedToRollback.getCredit() > 0) {
//      amount = leaderboardTransactionLogNeedToRollback.getCredit();
//    }
//
//    if (leaderboardTransactionLogNeedToRollback.getDebit() > 0) {
//      CampaignScoreRequest campaignScoreRequest = new CampaignScoreRequest();
//      campaignScoreRequest.setPsId(psId);
//      campaignScoreRequest.setHash(hash);
//      campaignScoreRequest.setCampaignUniqueCode(leaderboardTransactionLogNeedToRollback
//          .getCampaignUniqueCode());
//      campaignScoreRequest.setActivity(ROLLBACK_ACTIVITY);
//      campaignScoreRequest.setActivityObject(ROLLBACK_ACTIVITY_OBJECT_LEADERBOARD);
//      campaignScoreRequest.setObjectId(leaderboardTransactionLogNeedToRollback.getId());
//      campaignScoreRequest.setClientTransactionId(leaderboardTransactionLogNeedToRollback
//          .getClientTransactionId());
//      campaignScoreRequest.setDescription(description);
//      campaignScoreRequest.setAdditionalData(reason);
//      campaignScoreRequest.setScore(amount);
//
//      leaderboardAfterRollback = campaignService
//          .addScoretoLeaderboard(leaderboardTransactionLogNeedToRollback.getClientId(),
//              campaignScoreRequest);
//    } else if (leaderboardTransactionLogNeedToRollback.getCredit() > 0) {
//      CampaignScoreRequest campaignSubstractScoreRequest = new CampaignScoreRequest();
//      campaignSubstractScoreRequest.setPsId(psId);
//      campaignSubstractScoreRequest.setHash(hash);
//      campaignSubstractScoreRequest.setCampaignUniqueCode(leaderboardTransactionLogNeedToRollback
//          .getCampaignUniqueCode());
//      campaignSubstractScoreRequest.setActivity(ROLLBACK_ACTIVITY);
//      campaignSubstractScoreRequest.setActivityObject(ROLLBACK_ACTIVITY_OBJECT_LEADERBOARD);
//      campaignSubstractScoreRequest.setObjectId(leaderboardTransactionLogNeedToRollback.getId());
//      campaignSubstractScoreRequest.setClientTransactionId(leaderboardTransactionLogNeedToRollback
//          .getClientTransactionId());
//      campaignSubstractScoreRequest.setDescription(description);
//      campaignSubstractScoreRequest.setAdditionalData(reason);
//      campaignSubstractScoreRequest.setScore(amount);
//
//      leaderboardAfterRollback = campaignService
//          .substractScoretoLeaderboard(leaderboardTransactionLogNeedToRollback.getClientId(),
//              campaignSubstractScoreRequest);
//    }
//
//    return leaderboardAfterRollback;
//  }

  /*
   * run this sql as init : insert into
   * client_consumer(ps_id,closing_balance_in_poin_system,closing_balance_in_woman_talk,is_match)
   * select a.ps_id,b.closing_balance,0, case when (b.closing_balance=0) then true else false end as
   * is_match from consumer a left join trialbalance b on a.trial_balance_id=b.id;
   */

  /**
   * Compare poin balance between GPoin's Database and Womantalk .
   */
  @HystrixCommand
  @Transactional
  public void checkPoinDiscrepanciesFromWomanTalk() {
    String plainCreds = String.format("%s:%s", womantalkAccessKey, womantalkSecretKey);
    String womanTalkAccessToken = getTokenFromClient(plainCreds, womanTalkAuthUrl);

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", womanTalkAccessToken);
    HttpEntity<?> headerEntity = new HttpEntity<>(headers);

    HttpEntity<WrapperJson> wtEntity;

    UriComponentsBuilder apiUrlBuilder =
        UriComponentsBuilder.fromHttpUrl(womantalkApiUrl + "consumer/info")
            .queryParam("psId", "");
    int pageNumber = 1;
    boolean isContainData = true;
    List<ClientConsumer> listClientConsumer;

    String psId;
    int idx = 1;
    int sumOfNotMatchBalance = 0;
    while (isContainData) {
      PageRequest pageRequest =
          new PageRequest(pageNumber - 1, Constant.MAXIMUM_NUM_CLIENT_DATA, Sort.Direction.ASC,
              "psId");
      Page<ClientConsumer> data =
          clientConsumerRepository.findAllByRegisterFrom("womantalk", pageRequest);
      listClientConsumer = data.getContent();
      Integer pointInClient;
      Boolean pointIsMatch;
      for (ClientConsumer cc : listClientConsumer) {
        psId = cc.getPsId();

        // send
        apiUrlBuilder.replaceQueryParam("psId", psId);

        wtEntity = restTemplate.exchange(apiUrlBuilder.build().encode().toUri(), HttpMethod.GET,
            headerEntity, WrapperJson.class);

        JsonData cwt = wtEntity.getBody().getData();

        if (cwt != null) {
          pointInClient = (cwt.getPoint() != null) ? cwt.getPoint() : 0;

          if (cc.getClosingBalanceInPoinSystem().equals(pointInClient)) {
            pointIsMatch = true;
          } else {
            pointIsMatch = false;
            sumOfNotMatchBalance++;
          }

          cc.setClosingBalanceInWomanTalk(pointInClient);
          cc.setIsMatch(pointIsMatch);

          clientConsumerRepository.save(cc);

          logger.info(idx + ". SINKRONISASI PSID " + psId + " POIN DI WT " + pointInClient
              + " POINT DI KITA " + cc.getClosingBalanceInPoinSystem());

          idx++;
        }

      }

      pageNumber++;
      isContainData = listClientConsumer.size() >= Constant.MAXIMUM_NUM_CLIENT_DATA;
    }

    if (sumOfNotMatchBalance > 0) {
      emailBlaster.send("senoaji.wijaya@gvmnetworks.com", "[BALANCE CHECK REPORT]",
          sumOfNotMatchBalance + " RECORDS DO NOT MATCH");
    }
  }

  private static String getTokenFromClient(String plainCreds, String authUrl) {
    RestTemplate restTemplate = new RestTemplate();

    byte[] plainCredsBytes = plainCreds.getBytes(Charset.forName("UTF-8"));
    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    String base64Creds = new String(base64CredsBytes, Charset.forName("UTF-8"));

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic " + base64Creds);

    AuthToken authToken;

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", "client_credentials");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

    ResponseEntity<AuthToken> tokenData =
        restTemplate.exchange(authUrl, HttpMethod.POST, request, AuthToken.class);
    authToken = tokenData.getBody();

    return "Bearer " + authToken.getAccessToken();
  }
}
