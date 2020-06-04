package org.gvm.product.gvmpoin.module.continuousengagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.gvm.product.gvmpoin.module.campaign.Campaign;
import org.gvm.product.gvmpoin.module.campaign.CampaignAddCountRequest;
import org.gvm.product.gvmpoin.module.campaign.CampaignService;
import org.gvm.product.gvmpoin.module.campaign.StubCampaignRepository;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.Leaderboard;
import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.common.exception.HashNotValidException;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.Progressbar;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.ProgressbarInactiveException;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.ProgressbarInvalidClientException;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.ProgressbarNotFoundException;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.ProgressbarRepository;
import org.gvm.product.gvmpoin.module.tiersystem.TierSystemService;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalance;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceRepository;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.gvm.product.gvmpoin.module.common.Constant.SALT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by sofian.hadianto on 2/21/2017.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class ContinuousEngagementServiceTest {

  private static final String CLIENTID = "womantalk";
  private static final String CLIENTSECRET = "abcdef";
  private static final String CLIENTREDIRECTURI = "womantalk.com";
  private static final String CLIENTEMAIL = "womantalk@womantalk.com";

  private static final String UNAUTHORIZEDCLIENTID = "katakita";

  private static final String ACTIVITY = "SHARE";
  private static final String ACTIVITYOBJECT = "ARTICLE";
  private static final Long OBJECTID = 50L;
  private static final String ADDITIONALDATA = "ADDITIONAL DATA";

  private static final String EMAIL = "sofian.hadianto@gdplabs.co";
  private static final String PSID = "276555692384";
  private static final String FAKEPSID = "1234567890";
  private static final int ACTIVE = 1;
  private static final String HASH = "6ad812df0378cbf9aba014ed33541442";
  private static final String FAKEHASH = "41d1a369a47321b2bd94c13f73439778";

  private static final String CAMPAIGNNAME = "Campaign Jajal";
  private static final String CAMPAIGNDESC = "Deskripsi";
  private static final String CAMPAIGNUNIQUECODE = "campaign_mB6fs88l7u6nL1J73B3rENKKKmb08O";
  private static final int CAMPAIGNACTIVE = 1;

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ProgressbarRepository progressbarRepository;

  @Autowired
  private MyProgressRepository myProgressRepository;

  @Autowired
  private ConsumerRepository consumerRepository;

  @Autowired
  private MyProgressLogRepository myProgressLogRepository;

  @Autowired
  private TrialBalanceRepository trialBalanceRepository;

  @MockBean
  private Md5PasswordEncoder md5PasswordEncoder;

  @MockBean
  private TrialBalanceService trialBalanceService;

  @MockBean
  private CampaignService campaignService;

  @MockBean
  private TierSystemService tierSystemService;

  private Progressbar progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue;
  private Progressbar progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsFalse;
  private Progressbar progressbarWithoutCampaignAndLimitOneCountPerDayIsFalseAndActiveIsTrue;
  private MyProgress myProgress;
  private Consumer consumer;
  private Client client;
  private TrialBalance trialBalance;
  private ContinuousEngagementService continuousEngagementService;

  @Before
  public void setUp() throws Exception {
    this.generateSampleClient();
    this.generateSampleConsumer();
    this.generateSampleTrialBalance();
    this.generateSampleCampaign();
    this.generateSampleLeaderboard();
    this.generateSampleProgressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndStatusActiveTrue();
    this.generateSampleProgressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndStatusActiveFalse();
    this.generateSampleProgressbarWithoutCampaignAndLimitOneCountPerDayIsFalseAndStatusActiveTrue();
    when(md5PasswordEncoder.isPasswordValid(HASH, PSID, SALT)).thenReturn(true);
    when(md5PasswordEncoder.isPasswordValid(FAKEHASH, FAKEPSID, SALT)).thenReturn(true);
  }

  @After
  public void tearDown() throws Exception {
    progressbarRepository.deleteAll();
  }

  @Test
  @Ignore
  public void testAddCountNormalCase() {
    CampaignAddCountRequest campaignAddCountRequest = new CampaignAddCountRequest();
    campaignAddCountRequest.setProgressbarId(
        progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue.getId());
    campaignAddCountRequest.setPsId(PSID);
    campaignAddCountRequest.setHash(HASH);
    campaignAddCountRequest.setActivity(ACTIVITY);
    campaignAddCountRequest.setActivityObject(ACTIVITYOBJECT);
    campaignAddCountRequest.setObjectId(OBJECTID);
    campaignAddCountRequest.setAdditionalData(ADDITIONALDATA);
    campaignAddCountRequest.setClientId(CLIENTID);

    continuousEngagementService = new ContinuousEngagementService(myProgressRepository,
        progressbarRepository
        , consumerRepository, campaignService
        , md5PasswordEncoder, trialBalanceService
        , myProgressLogRepository, trialBalanceRepository, tierSystemService);

    //myProgress = continuousEngagementService.addCount(campaignAddCountRequest);
    assertEquals(myProgress.getClosingBalance().intValue(), 1);
  }

  @Test
  @Ignore
  public void testAddCountMultipleTimesWhenLimitOneCountPerDayIsTrue() {
    CampaignAddCountRequest campaignAddCountRequest = new CampaignAddCountRequest();
    campaignAddCountRequest.setProgressbarId(
        progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue.getId());
    campaignAddCountRequest.setPsId(PSID);
    campaignAddCountRequest.setHash(HASH);
    campaignAddCountRequest.setActivity(ACTIVITY);
    campaignAddCountRequest.setActivityObject(ACTIVITYOBJECT);
    campaignAddCountRequest.setObjectId(OBJECTID);
    campaignAddCountRequest.setAdditionalData(ADDITIONALDATA);
    campaignAddCountRequest.setClientId(CLIENTID);

    continuousEngagementService = new ContinuousEngagementService(myProgressRepository,
        progressbarRepository
        , consumerRepository, campaignService
        , md5PasswordEncoder, trialBalanceService
        , myProgressLogRepository, trialBalanceRepository, tierSystemService);

//    myProgress = continuousEngagementService.addCount(campaignAddCountRequest);
//    myProgress = continuousEngagementService.addCount(campaignAddCountRequest);
    assertEquals(myProgress.getClosingBalance().intValue(), 1);
  }

  @Test(expected = ProgressbarNotFoundException.class)
  @Ignore
  public void testAddCountWithInvalidProgressbarId() {
    CampaignAddCountRequest campaignAddCountRequest = new CampaignAddCountRequest();
    campaignAddCountRequest.setProgressbarId(-1L);
    campaignAddCountRequest.setPsId(PSID);
    campaignAddCountRequest.setHash(HASH);
    campaignAddCountRequest.setActivity(ACTIVITY);
    campaignAddCountRequest.setActivityObject(ACTIVITYOBJECT);
    campaignAddCountRequest.setObjectId(OBJECTID);
    campaignAddCountRequest.setAdditionalData(ADDITIONALDATA);
    campaignAddCountRequest.setClientId(CLIENTID);

    continuousEngagementService = new ContinuousEngagementService(myProgressRepository,
        progressbarRepository
        , consumerRepository, campaignService
        , md5PasswordEncoder, trialBalanceService
        , myProgressLogRepository, trialBalanceRepository, tierSystemService);

    //myProgress = continuousEngagementService.addCount(campaignAddCountRequest);
    assertEquals(myProgress.getClosingBalance().intValue(), 1);
  }

  @Test(expected = ProgressbarInvalidClientException.class)
  @Ignore
  public void testAddCountWithUnauthorizedClientId() {
    CampaignAddCountRequest campaignAddCountRequest = new CampaignAddCountRequest();
    campaignAddCountRequest.setProgressbarId(
        progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue.getId());
    campaignAddCountRequest.setPsId(PSID);
    campaignAddCountRequest.setHash(HASH);
    campaignAddCountRequest.setActivity(ACTIVITY);
    campaignAddCountRequest.setActivityObject(ACTIVITYOBJECT);
    campaignAddCountRequest.setObjectId(OBJECTID);
    campaignAddCountRequest.setAdditionalData(ADDITIONALDATA);
    campaignAddCountRequest.setClientId(UNAUTHORIZEDCLIENTID);

    continuousEngagementService = new ContinuousEngagementService(myProgressRepository,
        progressbarRepository
        , consumerRepository, campaignService
        , md5PasswordEncoder, trialBalanceService
        , myProgressLogRepository, trialBalanceRepository, tierSystemService);

    //myProgress = continuousEngagementService.addCount(campaignAddCountRequest);
    assertEquals(myProgress.getClosingBalance().intValue(), 1);
  }

  @Test(expected = HashNotValidException.class)
  @Ignore
  public void testAddCountWithInvalidHash() {
    CampaignAddCountRequest campaignAddCountRequest = new CampaignAddCountRequest();
    campaignAddCountRequest.setProgressbarId(
        progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue.getId());
    campaignAddCountRequest.setPsId(PSID);
    campaignAddCountRequest.setHash(FAKEHASH);
    campaignAddCountRequest.setActivity(ACTIVITY);
    campaignAddCountRequest.setActivityObject(ACTIVITYOBJECT);
    campaignAddCountRequest.setObjectId(OBJECTID);
    campaignAddCountRequest.setAdditionalData(ADDITIONALDATA);
    campaignAddCountRequest.setClientId(CLIENTID);

    continuousEngagementService = new ContinuousEngagementService(myProgressRepository,
        progressbarRepository
        , consumerRepository, campaignService
        , md5PasswordEncoder, trialBalanceService
        , myProgressLogRepository, trialBalanceRepository, tierSystemService);

   // myProgress = continuousEngagementService.addCount(campaignAddCountRequest);
    assertEquals(myProgress.getClosingBalance().intValue(), 1);
  }

  @Test(expected = ProgressbarInactiveException.class)
  @Ignore
  public void testAddCountInInactiveProgressbar() {
    CampaignAddCountRequest campaignAddCountRequest = new CampaignAddCountRequest();
    campaignAddCountRequest.setProgressbarId(
        progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsFalse.getId());
    campaignAddCountRequest.setPsId(PSID);
    campaignAddCountRequest.setHash(HASH);
    campaignAddCountRequest.setActivity(ACTIVITY);
    campaignAddCountRequest.setActivityObject(ACTIVITYOBJECT);
    campaignAddCountRequest.setObjectId(OBJECTID);
    campaignAddCountRequest.setAdditionalData(ADDITIONALDATA);
    campaignAddCountRequest.setClientId(CLIENTID);

    continuousEngagementService = new ContinuousEngagementService(myProgressRepository,
        progressbarRepository
        , consumerRepository, campaignService
        , md5PasswordEncoder, trialBalanceService
        , myProgressLogRepository, trialBalanceRepository, tierSystemService);

   // myProgress = continuousEngagementService.addCount(campaignAddCountRequest);
    assertEquals(myProgress.getClosingBalance().intValue(), 1);
  }

  @Test
  @Ignore
  public void testAddCountMultipleTimesWhenLimitOneCountPerDayIsFalse() {
    CampaignAddCountRequest campaignAddCountRequest = new CampaignAddCountRequest();
    campaignAddCountRequest.setProgressbarId(
        progressbarWithoutCampaignAndLimitOneCountPerDayIsFalseAndActiveIsTrue.getId());
    campaignAddCountRequest.setPsId(PSID);
    campaignAddCountRequest.setHash(HASH);
    campaignAddCountRequest.setActivity(ACTIVITY);
    campaignAddCountRequest.setActivityObject(ACTIVITYOBJECT);
    campaignAddCountRequest.setObjectId(OBJECTID);
    campaignAddCountRequest.setAdditionalData(ADDITIONALDATA);
    campaignAddCountRequest.setClientId(CLIENTID);

    continuousEngagementService = new ContinuousEngagementService(myProgressRepository,
        progressbarRepository
        , consumerRepository, campaignService
        , md5PasswordEncoder, trialBalanceService
        , myProgressLogRepository, trialBalanceRepository, tierSystemService);

//    myProgress = continuousEngagementService.addCount(campaignAddCountRequest);
//    myProgress = continuousEngagementService.addCount(campaignAddCountRequest);

    assertEquals(myProgress.getClosingBalance().intValue(), 2);
  }

  private List<Goal> generateSampleGoal(Progressbar progressbar) {
    final int MAXIMUM_SAMPLE = 3;
    List<Goal> goalNeedToAchievedUnderProgressbar = new ArrayList<>();
    for (int i = 1; i <= MAXIMUM_SAMPLE; i++) {
      Goal goal = new Goal();
      goal.setProgressbar(progressbar);
      goal.setMaximumCount(MAXIMUM_SAMPLE * i);
      goal.setRewardPoint(10 * i);

      goalNeedToAchievedUnderProgressbar.add(goal);
    }

    return goalNeedToAchievedUnderProgressbar;
  }

  private void generateSampleProgressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndStatusActiveTrue() {
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue = new Progressbar();
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue.setClient(client);
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue
        .setCreatedTime(new Date());
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue
        .setIsLimitOneCountPerDay(true);

    List<Goal> goalNeedToAchievedUnderProgressbar = this
        .generateSampleGoal(progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue);
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue
        .setGoals(goalNeedToAchievedUnderProgressbar);
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue
        .setLatestUpdatedTime(new Date());
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue.setStatusActive(true);
    entityManager.persist(progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsTrue);
  }

  private void generateSampleProgressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndStatusActiveFalse() {
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsFalse = new Progressbar();
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsFalse.setClient(client);
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsFalse
        .setCreatedTime(new Date());
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsFalse
        .setIsLimitOneCountPerDay(true);

    List<Goal> goalNeedToAchievedUnderProgressbar = this
        .generateSampleGoal(progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsFalse);
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsFalse
        .setGoals(goalNeedToAchievedUnderProgressbar);
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsFalse
        .setLatestUpdatedTime(new Date());
    progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsFalse.setStatusActive(false);
    entityManager.persist(progressbarWithoutCampaignAndLimitOneCountPerDayIsTrueAndActiveIsFalse);
  }

  private void generateSampleProgressbarWithoutCampaignAndLimitOneCountPerDayIsFalseAndStatusActiveTrue() {
    progressbarWithoutCampaignAndLimitOneCountPerDayIsFalseAndActiveIsTrue = new Progressbar();
    progressbarWithoutCampaignAndLimitOneCountPerDayIsFalseAndActiveIsTrue.setClient(client);
    progressbarWithoutCampaignAndLimitOneCountPerDayIsFalseAndActiveIsTrue
        .setCreatedTime(new Date());
    progressbarWithoutCampaignAndLimitOneCountPerDayIsFalseAndActiveIsTrue
        .setIsLimitOneCountPerDay(false);

    List<Goal> goalNeedToAchievedUnderProgressbar = this
        .generateSampleGoal(progressbarWithoutCampaignAndLimitOneCountPerDayIsFalseAndActiveIsTrue);
    progressbarWithoutCampaignAndLimitOneCountPerDayIsFalseAndActiveIsTrue
        .setGoals(goalNeedToAchievedUnderProgressbar);
    progressbarWithoutCampaignAndLimitOneCountPerDayIsFalseAndActiveIsTrue
        .setLatestUpdatedTime(new Date());
    progressbarWithoutCampaignAndLimitOneCountPerDayIsFalseAndActiveIsTrue.setStatusActive(true);
    entityManager.persist(progressbarWithoutCampaignAndLimitOneCountPerDayIsFalseAndActiveIsTrue);
  }

  private void generateSampleClient() {
    client = new Client();
    client.setClientId(CLIENTID);
    client.setClientSecret(CLIENTSECRET);
    client.setWebServerRedirectUri(CLIENTREDIRECTURI);
    client.setEmail(CLIENTEMAIL);
    client.setRegistrationTime(new Date());
    client.setApproved(true);
    entityManager.persist(client);
  }

  private void generateSampleTrialBalance() {
    trialBalance = new TrialBalance();
    trialBalance.setOpeningBalance(0);
    trialBalance.setTotalDebits(0);
    trialBalance.setTotalCredits(0);
    trialBalance.setClosingBalance(0);
    trialBalance.setLastUpdatedTime(new Date());
    trialBalance.setOwner(consumer);
    entityManager.persist(trialBalance);
  }

  private void generateSampleConsumer() {
    consumer = new Consumer();
    consumer.setPsId(PSID);
    consumer.setEmail(EMAIL);
    consumer.setStatus(ACTIVE);
    consumer.setRegisterFrom(client);
    consumer.setTrialBalance(trialBalance);
    consumer.setRegisterTime(new Date());
    consumer.setEmailVerified(true);
    entityManager.persist(consumer);
  }

  private void generateSampleCampaign() {
    Date createdDate = new Date();
    Date startDate = DateUtils.addDays(createdDate, 1);
    Date endDate = DateUtils.addDays(startDate, 30);
    Date archiveExpDate = DateUtils.addDays(startDate, 60);
    Campaign campaign = new Campaign();
    campaign.setTitle(CAMPAIGNNAME);
    campaign.setCreatedDate(createdDate);
    campaign.setDescription(CAMPAIGNDESC);
    campaign.setStartDate(startDate);
    campaign.setEndDate(endDate);
    campaign.setCampaignUniqueCode(CAMPAIGNUNIQUECODE);
    campaign.setStatus(CAMPAIGNACTIVE);
    campaign.setUpdatedDate(startDate);
    campaign.setClientId(client);
    campaign.setArchivedExpirationDate(archiveExpDate);
    entityManager.persist(campaign);
  }

  private void generateSampleLeaderboard() {
    Leaderboard leaderboard = new Leaderboard();
    leaderboard.setPsId(PSID);
    leaderboard.setCampaign(StubCampaignRepository.buildCampaign());
    leaderboard.setRank(null);
    leaderboard.setOpeningBalance(0);
    leaderboard.setTotalDebitMutation(0);
    leaderboard.setTotalCreditMutation(0);
    leaderboard.setClosingBalance(0);
    leaderboard.setLastUpdatedTime(new Date());

    entityManager.persist(leaderboard);
  }
}
