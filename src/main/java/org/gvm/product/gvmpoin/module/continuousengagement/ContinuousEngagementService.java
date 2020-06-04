package org.gvm.product.gvmpoin.module.continuousengagement;

import org.apache.commons.lang3.time.DateUtils;
import org.gvm.product.gvmpoin.module.campaign.Campaign;
import org.gvm.product.gvmpoin.module.campaign.CampaignAddCountRequest;
import org.gvm.product.gvmpoin.module.campaign.CampaignScoreRequest;
import org.gvm.product.gvmpoin.module.campaign.CampaignService;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.Leaderboard;
import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.exception.HashNotValidException;
import org.gvm.product.gvmpoin.module.common.exception.NegativeNumberException;
import org.gvm.product.gvmpoin.module.common.exception.PsIdNotFoundException;
import org.gvm.product.gvmpoin.module.common.exception.PsIdTemporarySuspendedException;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.Progressbar;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.ProgressbarInactiveException;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.ProgressbarInvalidClientException;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.ProgressbarNotFoundException;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.ProgressbarRepository;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntry;
import org.gvm.product.gvmpoin.module.tiersystem.TierSystemService;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalance;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceNotFoundException;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceParam;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceParam.Builder;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceRepository;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.TimeZone;

import javax.transaction.Transactional;

@Service
public class ContinuousEngagementService {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private static final String DESCRIPTION_ADD_COUNT = "ADD_COUNT_FROM_PROGRESSBAR-%s";
  private static final String DESCRIPTION_LAST_GOAL_ACHIEVED_RESET_COUNT =
      "LAST_GOAL_ACHIEVED_RESET_COUNT_FROM_PROGRESSBAR-%s";
  private static final String DESCRIPTION_END_OF_DAY_INACTIVITY_RESET_COUNT =
      "END_OF_DAY_INACTIVITY_RESET_COUNT_FROM_PROGRESSBAR-%s";

  private static final String ACTIVITY_RESET_COUNT = "RESET_COUNT";

  // reguler point
  private static final String DESCRIPTION_ADD_POINT = "ADD_POINT_FROM_PROGRESSBAR-%s";

  // campaign point
  private static final String DESCRIPTION_ADD_SCORE = "ADD_SCORE_FROM_PROGRESSBAR-%s";

  private ProgressbarRepository progressbarRepository;
  private MyProgressRepository myProgressRepository;
  private ConsumerRepository consumerRepository;
  private CampaignService campaignService;
  private TrialBalanceService trialBalanceService;
  private Md5PasswordEncoder md5PasswordEncoder;
  private MyProgressLogRepository myProgressLogRepository;
  private TrialBalanceRepository trialBalanceRepository;
  private TierSystemService tierSystemService;

  /**
   * Bean Configuration for Continuous Engagement .
   *
   * @param myProgressRepository My Progress Interface
   * @param progressbarRepository Progress Bar Interface
   * @param consumerRepository Consumer Interface
   * @param campaignService Campaign Implementation
   * @param md5PasswordEncoder MD5 Password Encoder Implementation
   * @param trialBalanceService Trial Balance Implementation
   * @param myProgressLogRepository My Progress Log Interface
   * @param trialBalanceRepository Trial Balance Interface
   * @param tierSystemService Tier System Implementation
   */
  @Autowired
  public ContinuousEngagementService(MyProgressRepository myProgressRepository,
      ProgressbarRepository progressbarRepository, ConsumerRepository consumerRepository,
      CampaignService campaignService, Md5PasswordEncoder md5PasswordEncoder,
      TrialBalanceService trialBalanceService, MyProgressLogRepository myProgressLogRepository,
      TrialBalanceRepository trialBalanceRepository, TierSystemService tierSystemService) {
    this.myProgressRepository = myProgressRepository;
    this.progressbarRepository = progressbarRepository;
    this.consumerRepository = consumerRepository;
    this.campaignService = campaignService;
    this.md5PasswordEncoder = md5PasswordEncoder;
    this.trialBalanceService = trialBalanceService;
    this.myProgressLogRepository = myProgressLogRepository;
    this.trialBalanceRepository = trialBalanceRepository;
    this.tierSystemService = tierSystemService;
  }

//  @Transactional
//  MyProgress addCount(CampaignAddCountRequest campaignAddCountRequest) {
//    logger.info("trying to add count for ps_id = " + campaignAddCountRequest.getPsId());
//    Progressbar progressbar = this.getProgressbar(campaignAddCountRequest.getProgressbarId());
//
//    if (!isActive(progressbar)) {
//      throw new ProgressbarInactiveException();
//    }
//
//    Consumer consumer = this.getConsumer(campaignAddCountRequest.getPsId());
//
//    if (!clientAuthorized(progressbar, campaignAddCountRequest.getClientId())) {
//      throw new ProgressbarInvalidClientException();
//    }
//
//    boolean matchHash = md5PasswordEncoder.isPasswordValid(campaignAddCountRequest.getHash(),
//        consumer.getPsId(), Constant.SALT);
//    if (!matchHash) {
//      throw new HashNotValidException(campaignAddCountRequest.getHash());
//    }
//
//    Optional<MyProgress> optMyProgress = myProgressRepository
//        .findOneByProgressbarIdandConsumerId(progressbar.getId(), consumer.getId());
//    MyProgress myProgress;
//    if (optMyProgress.isPresent()) { // if not exist, create it
//      myProgress = optMyProgress.get();
//    } else {
//      myProgress = new MyProgress();
//      myProgress.setConsumer(consumer);
//      myProgress.setProgressbar(progressbar);
//      myProgress.setOpeningBalance(0);
//      myProgress.setTotalDebitMutation(0);
//      myProgress.setTotalCreditMutation(0);
//      myProgress.setClosingBalance(0);
//      myProgress.setCreatedTime(new Date());
//      myProgress.setLatestUpdatedTime(new Date());
//      myProgress.setCurrentGoalAchieved(0);
//    }
//
//    // ----
//
//    Integer lastOpeningBalance = myProgress.getOpeningBalance();
//    Integer lastTotalDebits = myProgress.getTotalDebitMutation();
//    Integer lastTotalCredits = myProgress.getTotalCreditMutation();
//
//    myProgress.setOpeningBalance(lastOpeningBalance);
//    myProgress.setTotalDebitMutation(lastTotalDebits);
//
//    if (!progressbar.getIsLimitOneCountPerDay()) {
//      Integer nextTotalCredits = lastTotalCredits + 1;
//      Integer nextClosingBalance = lastOpeningBalance + nextTotalCredits - lastTotalDebits;
//
//      myProgress.setTotalCreditMutation(nextTotalCredits);
//      myProgress.setClosingBalance(nextClosingBalance);
//    } else {
//      boolean isThisFunctionCalledToday = this
//          .isAddCountCalledToday(progressbar.getId(), consumer.getId());
//
//      logger.info("limit one count per day is applied.");
//      logger.info("check if this function is have been called today. check result = "
//          + isThisFunctionCalledToday);
//
//      if (!isThisFunctionCalledToday) {
//        Integer nextTotalCredits = lastTotalCredits + 1;
//        Integer nextClosingBalance = lastOpeningBalance + nextTotalCredits - lastTotalDebits;
//
//        myProgress.setTotalCreditMutation(nextTotalCredits);
//        myProgress.setClosingBalance(nextClosingBalance);
//      }
//    }
//
//    // ----
//    /*
//    Integer currentCountInDb = myProgress.getClosingBalance();
//    Integer currentCountAfterIncrement = 0;
//    if (!progressbar.getIsLimitOneCountPerDay()) {
//      // increment each this function called
//      currentCountAfterIncrement = currentCountInDb + 1;
//    } else {
//      logger.info("limit one count per day is applied.");
//
//      boolean isThisFunctionCalledToday = this
//      .isAddCountCalledToday(progressbar.getId(), consumer.getId());
//      logger.info("check if this function is have been called today. check result
//      = "+isThisFunctionCalledToday);
//
//      if (isThisFunctionCalledToday == false ) {
//        currentCountAfterIncrement = currentCountInDb + 1;
//      }
//    }
//
//    myProgress.setClosingBalance(currentCountAfterIncrement);
//    */
//    myProgress = myProgressRepository.saveAndFlush(myProgress);
//
//    //currentCountAfterIncrement
//    MyProgressLog myProgressLog = this.createMyProgressLogForAdd(campaignAddCountRequest,
//        myProgress.getClosingBalance(), myProgress.getId());
//
//    myProgressLogRepository.save(myProgressLog);
//
//    myProgress = this.reachingGoal(myProgress, campaignAddCountRequest);
//    return myProgress;
//  }

  MyProgress getCurrenCountByPsId(String clientId, Long progressbarId, String psId,
      String hash) {
    logger.info("fetching current progress for user with ps_id " + psId + " in progressbar "
        + progressbarId);

    boolean matchHash = md5PasswordEncoder.isPasswordValid(hash, psId, Constant.SALT);
    if (!matchHash) {
      throw new HashNotValidException(hash);
    }

    Progressbar progressbar = this.getProgressbar(progressbarId);

    if (!isActive(progressbar)) {
      throw new ProgressbarInactiveException();
    }

    if (!clientAuthorized(progressbar, clientId)) {
      throw new ProgressbarInvalidClientException();
    }

    Optional<MyProgress> optMyProgress = myProgressRepository
        .findOneByProgressbarIdandPsId(progressbarId, psId);

    MyProgress myProgress = new MyProgress();
    if (optMyProgress.isPresent()) {
      myProgress = optMyProgress.get();
    }

    return myProgress;
  }

  private Progressbar getProgressbar(Long progressbarId) {
    return progressbarRepository.findOneById(progressbarId)
        .orElseThrow(ProgressbarNotFoundException::new);
  }

  private Consumer getConsumer(String psId) {
    return consumerRepository.findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));
  }

  private static boolean isActive(Progressbar progressbar) {
    return progressbar.getStatusActive();
  }

  private static boolean clientAuthorized(Progressbar progressbar, String clientId) {
    Client client = progressbar.getClient();

    return client.getClientId().equals(clientId);

  }

  private boolean isAddCountCalledToday(Long progressbarId, Long consumerId) {
    Optional<MyProgress> optMyProgress = myProgressRepository
        .findOneByProgressbarIdandConsumerId(progressbarId, consumerId);

    boolean check = false;
    if (optMyProgress.isPresent()) {
      MyProgress myProgress = optMyProgress.get();
      Date currentDay = new Date();
      Date lastUpdate = myProgress.getLatestUpdatedTime();
      check = DateUtils.isSameDay(currentDay, lastUpdate);
    }

    return check;
  }

//  private MyProgress reachingGoal(MyProgress myProgress,
//      CampaignAddCountRequest campaignAddCountRequest) {
//    int currentCountAfterIncrement = myProgress.getClosingBalance();
//    Progressbar progressbar = myProgress.getProgressbar();
//    Client client = progressbar.getClient();
//    Consumer consumer = myProgress.getConsumer();
//    List<CampaignUnderProgressbar> campaignsUnderProgressbar = progressbar.getAppliedToCampaigns();
//
//    List<Goal> listOfGoals = progressbar.getGoals();
//
//    StringJoiner strListOfGoals = new StringJoiner(",");
//    for (Goal goal : listOfGoals) {
//      strListOfGoals.add(String.valueOf(goal.getMaximumCount()));
//    }
//    myProgress.setStrListOfGoals(strListOfGoals.toString());
//
//    int min = 0;
//    int max = listOfGoals.size() - 1;
//    int guess;
//
//    int currentMaximumCount;
//    int currentRewardPoint;
//    int lastMaximumCount = listOfGoals.get(listOfGoals.size() - 1).getMaximumCount();
//
//    while (min <= max) {
//      guess = Math.floorDiv((min + max), 2);
//      currentMaximumCount = listOfGoals.get(guess).getMaximumCount();
//      currentRewardPoint = listOfGoals.get(guess).getRewardPoint();
//
//      if (currentMaximumCount == currentCountAfterIncrement) {
//        if (currentCountAfterIncrement == lastMaximumCount) { // reset count menjadi 0
//          MyProgressLog myProgressLog = this.createMyProgressLogForReset(campaignAddCountRequest,
//              myProgress.getClosingBalance(), myProgress.getId());
//
//          myProgressLogRepository.save(myProgressLog);
//
//          Integer currentOpeningBalance = myProgress.getOpeningBalance();
//          Integer currentTotalDebitMutation = myProgress.getTotalDebitMutation();
//          Integer currentTotalCreditMutation = myProgress.getTotalCreditMutation();
//
//          Integer nextTotalDebitMutation = currentTotalDebitMutation
//              + myProgress.getClosingBalance();
//          Integer nextClosingBalance = currentOpeningBalance + currentTotalCreditMutation
//              - nextTotalDebitMutation;
//
//          myProgress.setOpeningBalance(currentOpeningBalance);
//          myProgress.setTotalDebitMutation(nextTotalDebitMutation);
//          myProgress.setTotalCreditMutation(currentTotalCreditMutation);
//          myProgress.setClosingBalance(nextClosingBalance);
//        }
//        myProgress.setCurrentGoalAchieved(currentCountAfterIncrement);
//
//        Builder builder = new TrialBalanceParam.Builder(consumer.getPsId(),
//            campaignAddCountRequest.getHash(), currentRewardPoint,
//            campaignAddCountRequest.getActivity(), campaignAddCountRequest.getActivityObject(),
//            client.getClientId());
//
//        final TrialBalanceParam addBalanceParam = builder
//            .description(String.format(DESCRIPTION_ADD_POINT, progressbar.getId()))
//            .objectId(campaignAddCountRequest.getObjectId())
//            .additionalData(campaignAddCountRequest.getAdditionalData())
//            .build();
//
//        this.addRewardToRegulerPoin(addBalanceParam);
//
//        if (campaignsUnderProgressbar.size() > 0) {
//          for (CampaignUnderProgressbar campaignUnderProgressbar : campaignsUnderProgressbar) {
//            Campaign campaign = campaignUnderProgressbar.getCampaign();
//            this.addRewardToCampaignPoin(campaign.getCampaignUniqueCode(), campaignAddCountRequest,
//                currentRewardPoint);
//          }
//        }
//
//        return myProgress;
//      } else if (currentMaximumCount < currentCountAfterIncrement) {
//        min = guess + 1;
//      } else {
//        max = guess - 1;
//      }
//    }
//
//    return myProgress;
//  }

  private MyProgressLog createMyProgressLogForAdd(CampaignAddCountRequest campaignAddCountRequest,
      Integer closingBalance, Long myProgressId) {
    MyProgressLog myProgressLog = new MyProgressLog();
    myProgressLog.setBalance(closingBalance);
    myProgressLog.setClientId(campaignAddCountRequest.getClientId());
    myProgressLog.setDebit(0);
    myProgressLog.setCredit(1);
    myProgressLog.setDescription(String.format(DESCRIPTION_ADD_COUNT,
        campaignAddCountRequest.getProgressbarId()));
    myProgressLog.setMyProgressId(myProgressId);
    myProgressLog.setActivity(campaignAddCountRequest.getActivity());
    myProgressLog.setActivityObject(campaignAddCountRequest.getActivityObject());
    myProgressLog.setObjectId(campaignAddCountRequest.getObjectId());
    myProgressLog.setAdditionalData(campaignAddCountRequest.getAdditionalData());

    return myProgressLog;
  }

  private MyProgressLog createMyProgressLogForReset(
      CampaignAddCountRequest campaignAddCountRequest, Integer closingBalance,
      Long myProgressId) {
    MyProgressLog myProgressLog = new MyProgressLog();
    myProgressLog.setBalance(0);
    myProgressLog.setClientId(campaignAddCountRequest.getClientId());
    myProgressLog.setDebit(closingBalance);
    myProgressLog.setCredit(0);
    myProgressLog.setDescription(String.format(DESCRIPTION_LAST_GOAL_ACHIEVED_RESET_COUNT,
        campaignAddCountRequest.getProgressbarId()));
    myProgressLog.setMyProgressId(myProgressId);
    myProgressLog.setActivity(campaignAddCountRequest.getActivity());
    myProgressLog.setActivityObject(campaignAddCountRequest.getActivityObject());
    myProgressLog.setObjectId(campaignAddCountRequest.getObjectId());
    myProgressLog.setAdditionalData(campaignAddCountRequest.getAdditionalData());

    return myProgressLog;
  }

  @Transactional
  private void addRewardToRegulerPoin(TrialBalanceParam addBalanceParam) {
    if (addBalanceParam.getAmount() < 0) {
      throw new NegativeNumberException();
    }

    Optional<Consumer> checkConsumer = consumerRepository.findOneByPsId(addBalanceParam.getPsId());

    if (checkConsumer.isPresent()) {
      Consumer consumer = checkConsumer.get();
      boolean matchHash = md5PasswordEncoder.isPasswordValid(addBalanceParam.getHash(),
          consumer.getPsId(), Constant.SALT);

      if (consumer.getStatus().equals(Constant.FLAG_ACTIVE_PSID)) {
        if (matchHash) {
          Optional<TrialBalance> checkBalance = trialBalanceRepository
              .findTrialBalanceForUpdate(consumer.getTrialBalance().getId());

          if (checkBalance.isPresent()) {
            TrialBalance trialBalance = checkBalance.get();
            Optional<JournalEntry> optJournalEntry = trialBalanceService
                .createJournalEntryForCredit(trialBalance, addBalanceParam, checkConsumer.get());
            if (optJournalEntry.isPresent()) {
              tierSystemService.addLevelPointWhenTrialBalanceCredit(addBalanceParam);
            }

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

//  private void addRewardToCampaignPoin(String campaignUniqueCode,
//      CampaignAddCountRequest campaignAddCountRequest,
//      Integer score) {
//    logger.info("Sent campaign point with following request detail {}", campaignAddCountRequest);
//    Campaign campaign = campaignService.getCampaign(campaignUniqueCode);
//    boolean matchHash = md5PasswordEncoder.isPasswordValid(campaignAddCountRequest.getHash(),
//        campaignAddCountRequest.getPsId(), Constant.SALT);
//
//    Leaderboard leaderboard = new Leaderboard();
//    if (matchHash && !CampaignService.isCampaignInactive(campaign)
//        && !CampaignService.isCampaignClose(campaign)
//        && CampaignService.isAppliedToClient(campaign, campaignAddCountRequest.getClientId())
//        && score > 0) {
//      Consumer consumer = campaignService.getConsumer(campaignAddCountRequest.getPsId());
//
//      CampaignScoreRequest campaignScoreRequest = new CampaignScoreRequest();
//      campaignScoreRequest.setCampaignUniqueCode(campaignUniqueCode);
//      campaignScoreRequest.setPsId(campaignAddCountRequest.getPsId());
//      campaignScoreRequest.setScore(score);
//      campaignScoreRequest.setDescription(String.format(DESCRIPTION_ADD_SCORE,
//          campaignAddCountRequest.getProgressbarId()));
//      campaignScoreRequest.setHash(campaignAddCountRequest.getHash());
//      campaignScoreRequest.setActivity(campaignAddCountRequest.getActivity());
//      campaignScoreRequest.setActivityObject(campaignAddCountRequest.getActivityObject());
//      campaignScoreRequest.setObjectId(campaignAddCountRequest.getObjectId());
//      campaignScoreRequest.setAdditionalData(campaignAddCountRequest.getAdditionalData());
//
//      Optional<Leaderboard> optLeaderboard = campaignService.createLeaderboard(campaignScoreRequest,
//          consumer, campaign.getId());
//      if (optLeaderboard.isPresent()) {
//        leaderboard = optLeaderboard.get();
//      }
//
//      campaignService.buildNewLeaderboardTransactionLogForAdd(campaignAddCountRequest.getClientId(),
//          leaderboard, leaderboard.getClosingBalance(), campaignScoreRequest);
//      campaignService.calculateConsumerRankInCampaign(campaignScoreRequest.getCampaignUniqueCode());
//
//    }
//
//  }

  @Transactional
  public void resetCountToZeroWhenConsumerIdleInConsecutiveDays() {
    Calendar calender = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
    calender.add(Calendar.DAY_OF_MONTH, -1);
    Date yesterday = calender.getTime();
    Date startDate = getStartOfDate(yesterday);
    Date endDate = getEndOfDate(yesterday);

    List<Long> nonIdleMyProgressIds = myProgressLogRepository
        .findDistinctMyProgressIdByTransactionTimeBetweenAndCreditGreaterThanZero(startDate,
            endDate);

    final List<MyProgress> myProgressList;
    if (nonIdleMyProgressIds.isEmpty()) {
      myProgressList = myProgressRepository.findAllByClosingBalanceGreaterThanZero();
    } else {
      myProgressList = myProgressRepository
          .findAllByIdNotInAndClosingBalanceGreaterThanZero(nonIdleMyProgressIds);
    }
    myProgressList.forEach(this::resetCountToZeroForMyProgress);
  }

  private void resetCountToZeroForMyProgress(MyProgress myProgress) {
    Integer lastClosingBalance = myProgress.getClosingBalance();
    myProgress.setTotalDebitMutation(myProgress.getTotalDebitMutation() + lastClosingBalance);
    myProgress.setClosingBalance(0);
    myProgressRepository.saveAndFlush(myProgress);

    MyProgressLog myProgressLog = this
        .createMyProgressLogForEndOfDayInactivityResetCount(myProgress, lastClosingBalance);
    myProgressLogRepository.save(myProgressLog);
  }

  private MyProgressLog createMyProgressLogForEndOfDayInactivityResetCount(MyProgress myProgress,
      Integer closingBalance) {
    MyProgressLog myProgressLog = new MyProgressLog();
    myProgressLog.setBalance(0);
    myProgressLog.setClientId(myProgress.getProgressbar().getClient().getClientId());
    myProgressLog.setDebit(closingBalance);
    myProgressLog.setCredit(0);
    myProgressLog.setDescription(String.format(DESCRIPTION_END_OF_DAY_INACTIVITY_RESET_COUNT,
        myProgress.getProgressbar().getId()));
    myProgressLog.setMyProgressId(myProgress.getId());
    myProgressLog.setActivity(ACTIVITY_RESET_COUNT);

    return myProgressLog;
  }

  private static Date getStartOfDate(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }

  private static Date getEndOfDate(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);
    return cal.getTime();
  }
}
