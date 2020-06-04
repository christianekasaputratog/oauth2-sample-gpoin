package org.gvm.product.gvmpoin.module.trialbalance;

import org.apache.commons.codec.binary.Base64;
import org.gvm.product.gvmpoin.module.campaign.CampaignService;
import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.client.ClientConsumerRepository;
import org.gvm.product.gvmpoin.module.client.ClientRepository;
import org.gvm.product.gvmpoin.module.client.exception.ClientNotFoundException;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.JsonData;
import org.gvm.product.gvmpoin.module.common.WrapperJson;
import org.gvm.product.gvmpoin.module.common.exception.ExceededBalanceException;
import org.gvm.product.gvmpoin.module.common.exception.NegativeNumberException;
import org.gvm.product.gvmpoin.module.common.exception.PinException;
import org.gvm.product.gvmpoin.module.common.exception.PsIdNotFoundException;
import org.gvm.product.gvmpoin.module.common.exception.PsIdTemporarySuspendedException;
import org.gvm.product.gvmpoin.module.consumer.ClientConsumer;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.integrator.AuthToken;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntry;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntryRepository;
import org.gvm.product.gvmpoin.module.tiersystem.TierSystemService;
import org.gvm.product.gvmpoin.util.EmailBlaster;
import org.gvm.product.gvmpoin.util.MyPasswordEncoder;
import org.gvm.product.gvmpoin.util.SecurityUtil;
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
import java.util.stream.Collectors;

@Service
public class TrialBalanceService {

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

  private TrialBalanceRepository trialBalanceRepository;
  private JournalEntryRepository journalEntryRepository;
  private ConsumerRepository consumerRepository;
  //  private Md5PasswordEncoder md5PasswordEncoder;
  private MyPasswordEncoder myPasswordEncoder;
  private ClientRepository clientRepository;
  private CampaignService campaignService;
  private TierSystemService tierSystemService;
  private ClientConsumerRepository clientConsumerRepository;
  private EmailBlaster emailBlaster;
  private SecurityUtil securityUtil;

  @Value("${womantalk.auth}")
  private String womanTalkAuthUrl;
  @Value("${womantalk.api.url}")
  private String womantalkApiUrl;
  @Value("${womantalk.access_key}")
  private String womantalkAccessKey;
  @Value("${womantalk.secret_key}")
  private String womantalkSecretKey;

  /**
   * Bean Configuration for Trial Balance .
   *
   * @param trialBalanceRepository Trial Balance Interface
   * @param journalEntryRepository Journal Entry Interface
   * @param consumerRepository Consumer Interface
   * @param clientRepository Client Interface
   * @param campaignService Campaign Implementation
   * @param tierSystemService Tier System Implementation
   * @param clientConsumerRepository Client Consumer Interface
   * @param emailBlaster Email Blaster Implementation
   * @param securityUtil Security Util Implementation
   * @param myPasswordEncoder My Password Encoder Implementation
   */
  @Autowired
  public TrialBalanceService(
      TrialBalanceRepository trialBalanceRepository,
      JournalEntryRepository journalEntryRepository,
      ConsumerRepository consumerRepository, ClientRepository clientRepository,
      CampaignService campaignService,
      TierSystemService tierSystemService, ClientConsumerRepository clientConsumerRepository,
      EmailBlaster emailBlaster, SecurityUtil securityUtil, MyPasswordEncoder myPasswordEncoder) {
    this.trialBalanceRepository = trialBalanceRepository;
    this.journalEntryRepository = journalEntryRepository;
    this.consumerRepository = consumerRepository;
    this.clientRepository = clientRepository;
    this.campaignService = campaignService;
    this.tierSystemService = tierSystemService;
    this.clientConsumerRepository = clientConsumerRepository;
    this.emailBlaster = emailBlaster;
    this.securityUtil = securityUtil;
    this.myPasswordEncoder = myPasswordEncoder;
  }

  /**
   * Add GPoin Balance .
   *
   * @param addBalanceParam BuilderParam for add balance
   * @return Journal Entry's Detail
   */
  @Transactional
  public JournalEntry credit(TrialBalanceParam addBalanceParam) {
//    clientRepository.findOneByClientId(addBalanceParam.getClientId())
//        .orElseThrow(() -> new
//            ClientNotFoundException(addBalanceParam.getClientId()));

    if (addBalanceParam.getAmount() < 0) {
      throw new NegativeNumberException();
    }

    Optional<Consumer> checkConsumer = consumerRepository.findOneByPsId(addBalanceParam.getPsId());

    JournalEntry journalEntry = new JournalEntry();
    if (checkConsumer.isPresent()) {
      Consumer consumer = checkConsumer.get();

      if (consumer.getStatus().equals(Constant.FLAG_ACTIVE_PSID)) {
        isConsumerPinAuthenticated(addBalanceParam.getPassword(), consumer.getPassword());
        Optional<TrialBalance> checkBalance = trialBalanceRepository
            .findTrialBalanceForUpdate(consumer.getTrialBalance().getId());

        if (checkBalance.isPresent()) {
          TrialBalance trialBalance = checkBalance.get();
          addBalanceParam.setDescription(DESCRIPTION_ADD_POINT);
          Optional<JournalEntry> optJournalEntry = this.createJournalEntryForCredit(trialBalance,
              addBalanceParam, checkConsumer.get());
          if (optJournalEntry.isPresent()) {
            journalEntry = optJournalEntry.get();
            tierSystemService.addLevelPointWhenTrialBalanceCredit(addBalanceParam);
          }
          return journalEntry;
        } else {
          throw new TrialBalanceNotFoundException();
        }
      } else {
        throw new PsIdTemporarySuspendedException(addBalanceParam.getPsId());
      }
    } else {
      throw new PsIdNotFoundException(addBalanceParam.getPsId());
    }

  }

  /**
   * Create new journal entry transaction for Credit .
   *
   * @param trialBalance Detail trial balance
   * @param addBalanceParam Detail transaction request
   * @param consumer Detail Consumer
   * @return New Journal Entry Transaction
   */
  public Optional<JournalEntry> createJournalEntryForCredit(TrialBalance trialBalance,
      TrialBalanceParam addBalanceParam, Consumer consumer) {
    Integer lastOpeningBalance = trialBalance.getOpeningBalance();
    Integer lastTotalDebits = trialBalance.getTotalDebits();
    Integer lastTotalCredits = trialBalance.getTotalCredits();

    Integer nextTotalCredits = lastTotalCredits + addBalanceParam.getAmount();
    Integer nextClosingBalance = lastOpeningBalance + nextTotalCredits - lastTotalDebits;
    trialBalance.setOpeningBalance(lastOpeningBalance);
    trialBalance.setTotalDebits(lastTotalDebits);
    trialBalance.setTotalCredits(nextTotalCredits);
    trialBalance.setClosingBalance(nextClosingBalance);

    JournalEntry journalEntry = new JournalEntry();

    if (addBalanceParam.getAmount() > 0) {
      journalEntry.setClientId(addBalanceParam.getClientId());
      journalEntry.setDebit(0);
      journalEntry.setCredit(addBalanceParam.getAmount());
      journalEntry.setDescription(addBalanceParam.getDescription());
      journalEntry.setTrialBalance(trialBalance);
      journalEntry.setBalance(nextClosingBalance);
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

  /**
   * Debit Transaction .
   *
   * @param subtractBalanceParam Detail debit transaction request
   * @return Detail of Journal Entry
   */
  @Transactional
  public JournalEntry debit(TrialBalanceParam subtractBalanceParam) {
    if (subtractBalanceParam.getAmount() < 0) {
      throw new NegativeNumberException();
    }

    clientRepository.findOneByClientId(subtractBalanceParam.getClientId())
        .orElseThrow(() -> new ClientNotFoundException(subtractBalanceParam.getClientId()));

    Optional<Consumer> optionalConsumer = consumerRepository.findOneByPsId(
        subtractBalanceParam.getPsId());

    JournalEntry journalEntry = new JournalEntry();
    if (optionalConsumer.isPresent()) {
      Consumer consumer = optionalConsumer.get();

      if (consumer.getStatus().equals(Constant.FLAG_ACTIVE_PSID)) {
        isConsumerPinAuthenticated(subtractBalanceParam.getPassword(), consumer.getPassword());
        Optional<TrialBalance> checkBalance = trialBalanceRepository
            .findTrialBalanceForUpdate(consumer.getTrialBalance().getId());

        if (checkBalance.isPresent()) {
          TrialBalance trialBalance = checkBalance.get();

          if (subtractBalanceParam.getAmount() <= trialBalance.getClosingBalance()) {
            subtractBalanceParam.setDescription(DESCRIPTION_SUBSTRACT_POINT);
            Optional<JournalEntry> optJournalEntry = this
                .createJournalEntryForDebit(trialBalance, subtractBalanceParam,
                    optionalConsumer.get());

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
        throw new PsIdTemporarySuspendedException(subtractBalanceParam.getPsId());
      }

    } else {
      throw new PsIdNotFoundException(subtractBalanceParam.getPsId());
    }
  }

  private void isConsumerPinAuthenticated(String rawPin, String encodedPin) {
    if (encodedPin != null) {
      myPasswordEncoder.matches(rawPin, encodedPin);
    } else {
      throw new PinException();
    }
  }

  private Optional<JournalEntry> createJournalEntryForDebit(TrialBalance trialBalance,
      TrialBalanceParam subtractBalanceParam, Consumer consumer) {
    Integer lastOpeningBalance = trialBalance.getOpeningBalance();
    Integer lastTotalDebits = trialBalance.getTotalDebits();
    Integer lastTotalCredits = trialBalance.getTotalCredits();

    Integer nextTotalDebits = lastTotalDebits + subtractBalanceParam.getAmount();
    Integer nextClosingBalance = lastOpeningBalance + lastTotalCredits - nextTotalDebits;

    trialBalance.setOpeningBalance(lastOpeningBalance);
    trialBalance.setTotalDebits(nextTotalDebits);
    trialBalance.setTotalCredits(lastTotalCredits);
    trialBalance.setClosingBalance(nextClosingBalance);

    JournalEntry journalEntry = new JournalEntry();

    if (nextClosingBalance >= 0) {
      journalEntry.setClientId(subtractBalanceParam.getClientId());
      journalEntry.setDebit(subtractBalanceParam.getAmount());
      journalEntry.setCredit(0);
      journalEntry.setDescription(subtractBalanceParam.getDescription());
      journalEntry.setTrialBalance(trialBalance);
      journalEntry.setBalance(nextClosingBalance);
      journalEntry.setActivity(subtractBalanceParam.getActivity());
      journalEntry.setActivityObject(subtractBalanceParam.getActivityObject());
      journalEntry.setObjectId(subtractBalanceParam.getObjectId());
      journalEntry.setAdditionalData(subtractBalanceParam.getAdditionalData());
      journalEntry.setClientTransactionId(subtractBalanceParam.getClientTransactionId());
      journalEntry.setConsumer(consumer);

      if (subtractBalanceParam.getAmount() > 0) {
        journalEntry = journalEntryRepository.saveAndFlush(journalEntry);
      }
    }

    return Optional.ofNullable(journalEntry);
  }

  TrialBalance getTrialBalance(String psId, String pin) {
    Optional<Consumer> checkConsumer = consumerRepository.findOneByPsId(psId);

    TrialBalance trialBalance;
    if (checkConsumer.isPresent()) {
      Consumer consumer = checkConsumer.get();
      isConsumerPinAuthenticated(pin, checkConsumer.get().getPassword());
      trialBalance = consumer.getTrialBalance();
    } else {
      throw new PsIdNotFoundException(psId);
    }

    return trialBalance;
  }

  List<JournalEntry> getTransactionHistory(int pageNumber, int pageSize, String psId, String hash) {
    Consumer consumer = getConsumerByPsIdOrElseThrowException(psId);
    securityUtil.assertMatchHashForPsId(hash, psId);

    PageRequest pageRequest = new PageRequest(pageNumber - 1, pageSize, Sort.Direction.DESC,
        "id");
    Page<JournalEntry> data = journalEntryRepository.findByTrialBalanceId(
        consumer.getTrialBalance().getId(), pageRequest);

    for (JournalEntry journalEntry : data) {
      journalEntry.setTransactionActivity(getTransactionActivityByCondition(journalEntry));
    }

    List<Client> clients = clientRepository.findAll();

    return data.getContent()
        .stream()
        .peek(journalEntry -> journalEntry.setClient(filterClientsByClientId(clients,
            journalEntry.getClientId())))
        .collect(Collectors.toList());
  }

  private String getTransactionActivityByCondition(JournalEntry journalEntry) {
    String transactionActivity = journalEntry.getActivity() + " "
        + journalEntry.getActivityObject();

    if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForDailyLogin()
        .containsValue(journalEntry.getActivity())) {
      transactionActivity = TransactionActivity.DAILY_LOGIN.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForReadArticle()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.READ_ARTICLE.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForLikeArticle()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.LIKE_ARTICLE.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForShareArticle()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.SHARE_ARTICLE.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForCommentArticle()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.COMMENT_ARTICLE.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForCommentLiked()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.COMMENT_LIKED.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForTakeReward()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.TAKE_REWARD.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForRegistrationAccount()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.REGISTRATION_ACCOUNT.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForConversationLiked()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.CONVERSATION_LIKED.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForTakeSurvey()
        .containsValue(journalEntry.getActivityObject())) {
      transactionActivity = TransactionActivity.TAKE_SURVEY.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForCompleteProfile()
        .containsValue(journalEntry.getActivity())) {
      transactionActivity = TransactionActivity.COMPLETE_PROFILE.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForShareGallery()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.SHARE_GALLERY.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForJoinTrivia()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.JOIN_TRIVIA.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForWinTrivia()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.WIN_TRIVIA.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForShareTrivia()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.SHARE_TRIVIA.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForVerifyAccount()
        .containsValue(journalEntry.getActivity())) {
      transactionActivity = TransactionActivity.VERIFY_ACCOUNT.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForInputReferral()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.INPUT_REFERRAL.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForReceiveReferral()
        .containsValue(transactionActivity)) {
      transactionActivity = TransactionActivity.RECEIVE_REFERRAL.getActivity();
    } else if (TransactionActivity.TRANSACTION_ACTIVITY.buildMappingForExpertActivity()
        .containsValue(journalEntry.getActivityObject())) {
      transactionActivity = TransactionActivity.EXPERT_ACTIVITY.getActivity();
    }
    return transactionActivity;
  }

  private static Client filterClientsByClientId(List<Client> clients, String clientId) {
    return clients.stream()
        .filter(client -> client.getClientId().equals(clientId))
        .findFirst()
        .orElseThrow(() -> new ClientNotFoundException(clientId));
  }

  private Consumer getConsumerByPsIdOrElseThrowException(String psId) {
    return consumerRepository
        .findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));
  }

//  @Transactional
//  public RollbackTransaction rollbackTransaction(String clientTransactionId, String reason) {
//    JournalEntry rollbackJournalEntry = new JournalEntry();
//    String psId;
//    String hash;
//    Leaderboard rollbackLeaderboard = new Leaderboard();
//
//    if (!this.areYouHaveSentRollbackJournalEntry(clientTransactionId)) {
//      Optional<JournalEntry> optJournalEntry = journalEntryRepository
//          .findOneByClientTransactionId(clientTransactionId);
//      if (optJournalEntry.isPresent()) {
//        JournalEntry currentJournalEntry = optJournalEntry.get();
//        TrialBalance trialBalance = currentJournalEntry.getTrialBalance();
//        Consumer consumer = consumerRepository.findOneByTrialBalanceId(trialBalance.getId());
//
//        psId = consumer.getPsId();
//        hash = HashUtil.getHash(psId);
//        rollbackJournalEntry = this.createJournalEntryForRollback(psId, hash,
//            currentJournalEntry, reason);
//      }
//    }
//
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
//
//    return new RollbackTransaction(rollbackJournalEntry, rollbackLeaderboard);
//  }

//  private boolean areYouHaveSentRollbackJournalEntry(String clientTransactionId) {
//    Long count = journalEntryRepository.countRollbackTransaction(clientTransactionId);
//
//    return count > 0;
//  }
//
//  private JournalEntry createJournalEntryForRollback(String psId, String hash,
//      JournalEntry journalNeedToRollback,
//      String reason) {
//    int amount = 0;
//    JournalEntry journalAfterRollback = new JournalEntry();
//
//    logger.info("rollback transaction regular point.........");
//    String description = String.format(DESCRIPTION_ROLLBACK, CATEGORY_REGULAR_TRANSACTION,
//        journalNeedToRollback.getId());
//
//    if (journalNeedToRollback.getDebit() > 0) {
//      amount = journalNeedToRollback.getDebit();
//    } else if (journalNeedToRollback.getCredit() > 0) {
//      amount = journalNeedToRollback.getCredit();
//    }
//
//    TrialBalanceParam.Builder builder = new TrialBalanceParam.Builder(psId, hash, amount,
//        ROLLBACK_ACTIVITY, ROLLBACK_ACTIVITY_OBJECT_JOURNAL_ENTRY,
//        journalNeedToRollback.getClientId());
//
//    final TrialBalanceParam paramRollback = builder.description(description)
//        .objectId(journalNeedToRollback.getId())
//        .clientTransactionId(journalNeedToRollback.getClientTransactionId())
//        .additionalData(reason)
//        .build();
//
//    if (journalNeedToRollback.getDebit() > 0) {
//      journalAfterRollback = this.credit(paramRollback);
//    } else if (journalNeedToRollback.getCredit() > 0) {
//      journalAfterRollback = this.debit(paramRollback);
//    }
//
//    return journalAfterRollback;
//  }

//  private Leaderboard createLeaderboardForRollback(String psId, String hash,
//      LeaderboardTransactionLog leaderboardTransactionLog, String reason) {
//    Leaderboard leaderboardAfterRollback = new Leaderboard();
//    logger.info("rollback transaction leaderboard/campaign point.........");
//    String description = String.format(DESCRIPTION_ROLLBACK, CATEGORY_LEADERBOARD_TRANSACTION,
//        leaderboardTransactionLog.getId());
//
//    int amount = 0;
//    if (leaderboardTransactionLog.getDebit() > 0) {
//      amount = leaderboardTransactionLog.getDebit();
//    } else if (leaderboardTransactionLog.getCredit() > 0) {
//      amount = leaderboardTransactionLog.getCredit();
//    }
//
//    if (leaderboardTransactionLog.getDebit() > 0) {
//      CampaignScoreRequest campaignScoreRequest = new CampaignScoreRequest();
//      campaignScoreRequest.setPsId(psId);
//      campaignScoreRequest.setHash(hash);
//      campaignScoreRequest.setCampaignUniqueCode(leaderboardTransactionLog.getCampaignUniqueCode());
//      campaignScoreRequest.setActivity(ROLLBACK_ACTIVITY);
//      campaignScoreRequest.setActivityObject(ROLLBACK_ACTIVITY_OBJECT_LEADERBOARD);
//      campaignScoreRequest.setObjectId(leaderboardTransactionLog.getId());
//      campaignScoreRequest.setClientTransactionId(leaderboardTransactionLog
//          .getClientTransactionId());
//      campaignScoreRequest.setDescription(description);
//      campaignScoreRequest.setAdditionalData(reason);
//      campaignScoreRequest.setScore(amount);
//
//      leaderboardAfterRollback = campaignService.addScoretoLeaderboard(leaderboardTransactionLog
//          .getClientId(), campaignScoreRequest);
//    } else if (leaderboardTransactionLog.getCredit() > 0) {
//      CampaignScoreRequest campaignSubstractScoreRequest = new CampaignScoreRequest();
//      campaignSubstractScoreRequest.setPsId(psId);
//      campaignSubstractScoreRequest.setHash(hash);
//      campaignSubstractScoreRequest.setCampaignUniqueCode(leaderboardTransactionLog
//          .getCampaignUniqueCode());
//      campaignSubstractScoreRequest.setActivity(ROLLBACK_ACTIVITY);
//      campaignSubstractScoreRequest.setActivityObject(ROLLBACK_ACTIVITY_OBJECT_LEADERBOARD);
//      campaignSubstractScoreRequest.setObjectId(leaderboardTransactionLog.getId());
//      campaignSubstractScoreRequest.setClientTransactionId(leaderboardTransactionLog
//          .getClientTransactionId());
//      campaignSubstractScoreRequest.setDescription(description);
//      campaignSubstractScoreRequest.setAdditionalData(reason);
//      campaignSubstractScoreRequest.setScore(amount);
//
//      leaderboardAfterRollback = campaignService
//          .substractScoretoLeaderboard(leaderboardTransactionLog
//              .getClientId(), campaignSubstractScoreRequest);
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
          new PageRequest(pageNumber - 1, Constant.MAXIMUM_NUM_CLIENT_DATA,
              Sort.Direction.ASC, "psId");
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

    HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<>(map, headers);

    ResponseEntity<AuthToken> tokenData =
        restTemplate.exchange(authUrl, HttpMethod.POST, request, AuthToken.class);
    authToken = tokenData.getBody();

    return "Bearer " + authToken.getAccessToken();
  }
}
