package org.gvm.product.gvmpoin.module.tiersystem;

import java.util.Date;
import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalance;
import org.gvm.product.gvmpoin.util.DateUtil;
import org.gvm.product.gvmpoin.util.SecurityUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = TestConfig.class)
@DataJpaTest
public class TierSystemServiceIT {

  private static final String CLIENT_ID = "womantalk";
  private static final String CLIENT_SECRET = "abcdef";
  private static final String CLIENT_REDIRECT_URI = "womantalk.com";
  private static final String CLIENT_EMAIL = "womantalk@womantalk.com";
  private static final Integer MASTER_RESET_INTERVAL_INFINITE = -1;
  private static final String PSID = "276555692384";
  private static final String HASH = "6ad812df0378cbf9aba014ed33541442";
  private static final String CONSUMER_EMAIL = "bobbi.sinaga@gvmnetworks.com";
  private static final int CONSUMER_STATUS_ACTIVE = 1;
  private static final Integer LEVEL_POINT_100 = 100;
  private static final Integer ZERO_VALUE_INT = 0;
  private static final String REQUEST_ACTIVITY = "SHARE";
  private static final String REQUEST_ACTIVITY_OBJECT = "ARTICLE";
  private static final Long REQUEST_ACTIVITY_OBJECT_ID = 180L;
  private static final String REQUEST_ADDITIONAL_DATA = "Additional Data";

  @Autowired
  private TierSystemMasterRepository tierSystemMasterRepository;
  @Autowired
  private TierSystemLevelRepository tierSystemLevelRepository;
  @Autowired
  private TierSystemConsumerProgressRepository tierSystemConsumerProgressRepository;
  @Autowired
  private TierSystemConsumerProgressTransactionLogRepository
      tierSystemConsumerProgressTransactionLogRepository;
  @Autowired
  private ConsumerRepository consumerRepository;
  @MockBean
  private SecurityUtil securityUtil;
  @Autowired
  private TestEntityManager entityManager;

  private TierSystemService tierSystemService;

  private Client client;
  private TierSystemMaster master;
  private TierSystemLevel levelBronze;
  private TierSystemLevel levelSilver;
  private TierSystemLevel levelGold;
  private Consumer consumer;
  private TrialBalance trialBalance;

  @Before
  public void setUp() {
    setUpClient();
    setUpMaster();
    setUpLevels();
    setUpConsumerTrialBalance();
    setUpConsumer();
  }

  private void setUpMaster() {
    master = new TierSystemMaster();
    master.setClient(client);
    master.setStartDate(DateUtil.getTimeNow());
    master.setResetInterval(MASTER_RESET_INTERVAL_INFINITE);
    entityManager.persist(master);
  }

  private void setUpClient() {
    client = new Client();
    client.setClientId(CLIENT_ID);
    client.setClientSecret(CLIENT_SECRET);
    client.setWebServerRedirectUri(CLIENT_REDIRECT_URI);
    client.setEmail(CLIENT_EMAIL);
    client.setRegistrationTime(new Date());
    client.setApproved(true);
    entityManager.persist(client);
  }

  private void setUpLevels() {
    levelBronze = new TierSystemLevel();
    levelBronze.setTierSystemMaster(master);
    levelBronze.setLevel(1);
    levelBronze.setName("Bronze");
    levelBronze.setDescription("Level for Bronze");
    levelBronze.setLevelPointRequired(0);
    entityManager.persist(levelBronze);

    levelSilver = new TierSystemLevel();
    levelSilver.setTierSystemMaster(master);
    levelSilver.setLevel(2);
    levelSilver.setName("Silver");
    levelSilver.setDescription("Level for Silver");
    levelSilver.setLevelPointRequired(1000);
    entityManager.persist(levelSilver);

    levelGold = new TierSystemLevel();
    levelGold.setTierSystemMaster(master);
    levelGold.setLevel(3);
    levelGold.setName("Gold");
    levelGold.setDescription("Level for Gold");
    levelGold.setLevelPointRequired(2000);
    entityManager.persist(levelGold);
  }

  private void setUpConsumer() {
    consumer = new Consumer();
    consumer.setPsId(PSID);
    consumer.setEmail(CONSUMER_EMAIL);
    consumer.setStatus(CONSUMER_STATUS_ACTIVE);
    consumer.setRegisterFrom(client);
    consumer.setTrialBalance(trialBalance);
    consumer.setRegisterTime(new Date());
    consumer.setEmailVerified(true);
    entityManager.persist(consumer);
  }

  private void setUpConsumerTrialBalance() {
    trialBalance = new TrialBalance();
    trialBalance.setOpeningBalance(ZERO_VALUE_INT);
    trialBalance.setTotalDebits(ZERO_VALUE_INT);
    trialBalance.setTotalCredits(ZERO_VALUE_INT);
    trialBalance.setClosingBalance(ZERO_VALUE_INT);
    trialBalance.setLastUpdatedTime(DateUtil.getTimeNow());
    entityManager.persist(trialBalance);
  }

  @After
  public void tearDown() {
    tierSystemConsumerProgressTransactionLogRepository.deleteAll();
    tierSystemConsumerProgressRepository.deleteAll();
    tierSystemLevelRepository.deleteAll();
    tierSystemMasterRepository.deleteAll();
  }

  @Test
  public void testAddLevelPointForTheFirstTime() {
    tierSystemService = new TierSystemService(
        tierSystemMasterRepository, tierSystemLevelRepository, tierSystemConsumerProgressRepository,
        tierSystemConsumerProgressTransactionLogRepository, consumerRepository, securityUtil);

    TierSystemAddLevelPointRequest addLevelPointRequest = buildAddLevelPointRequest();

    TierSystemConsumerProgress consumerProgress = tierSystemService
        .addLevelPoint(addLevelPointRequest);

    assertEquals(ZERO_VALUE_INT, consumerProgress.getOpeningBalance());
    assertEquals(LEVEL_POINT_100, consumerProgress.getClosingBalance());
    assertEquals(ZERO_VALUE_INT, consumerProgress.getTotalDebitMutation());
    assertEquals(LEVEL_POINT_100, consumerProgress.getTotalCreditMutation());
    assertEquals(levelBronze, consumerProgress.getCurrentLevel());
  }

  @Test
  public void testAddLevelPointWhenNextLevelAchieved() {
    tierSystemService = new TierSystemService(
        tierSystemMasterRepository, tierSystemLevelRepository, tierSystemConsumerProgressRepository,
        tierSystemConsumerProgressTransactionLogRepository, consumerRepository, securityUtil);

    TierSystemLevel expectedNextLevel = levelSilver;
    TierSystemConsumerProgress consumerProgress;

    int addLevelPointCounter = 0;
    do {
      consumerProgress = tierSystemService.addLevelPoint(buildAddLevelPointRequest());
      addLevelPointCounter++;
    } while (consumerProgress.getClosingBalance() < expectedNextLevel.getLevelPointRequired());

    Integer expectedTotalCreditMutation = LEVEL_POINT_100 * addLevelPointCounter;
    Integer expectedClosingBalance = LEVEL_POINT_100 * addLevelPointCounter;

    assertEquals(ZERO_VALUE_INT, consumerProgress.getOpeningBalance());
    assertEquals(expectedClosingBalance, consumerProgress.getClosingBalance());
    assertEquals(ZERO_VALUE_INT, consumerProgress.getTotalDebitMutation());
    assertEquals(expectedTotalCreditMutation, consumerProgress.getTotalCreditMutation());
    assertEquals(expectedNextLevel, consumerProgress.getCurrentLevel());
  }

  @Test
  public void testAddLevelPointWhenMaxLevelAchieved() {
    tierSystemService = new TierSystemService(
        tierSystemMasterRepository, tierSystemLevelRepository, tierSystemConsumerProgressRepository,
        tierSystemConsumerProgressTransactionLogRepository, consumerRepository, securityUtil);

    TierSystemLevel expectedNextLevel = levelGold;
    TierSystemConsumerProgress consumerProgress;

    int addLevelPointCounter = 0;
    do {
      consumerProgress = tierSystemService.addLevelPoint(buildAddLevelPointRequest());
      addLevelPointCounter++;
    } while (consumerProgress.getClosingBalance() < expectedNextLevel.getLevelPointRequired());

    Integer expectedTotalCreditMutation = LEVEL_POINT_100 * addLevelPointCounter;
    Integer expectedClosingBalance = LEVEL_POINT_100 * addLevelPointCounter;

    assertEquals(ZERO_VALUE_INT, consumerProgress.getOpeningBalance());
    assertEquals(expectedClosingBalance, consumerProgress.getClosingBalance());
    assertEquals(ZERO_VALUE_INT, consumerProgress.getTotalDebitMutation());
    assertEquals(expectedTotalCreditMutation, consumerProgress.getTotalCreditMutation());
    assertEquals(expectedNextLevel, consumerProgress.getCurrentLevel());
  }

  @Test
  public void testAddLevelPointWhenMaxLevelExceeded() {
    tierSystemService = new TierSystemService(
        tierSystemMasterRepository, tierSystemLevelRepository, tierSystemConsumerProgressRepository,
        tierSystemConsumerProgressTransactionLogRepository, consumerRepository, securityUtil);

    TierSystemLevel expectedNextLevel = levelGold;
    TierSystemConsumerProgress consumerProgress;

    int addLevelPointCounter = 0;
    do {
      consumerProgress = tierSystemService.addLevelPoint(buildAddLevelPointRequest());
      addLevelPointCounter++;
    } while (consumerProgress.getClosingBalance() < expectedNextLevel.getLevelPointRequired());

    consumerProgress = tierSystemService.addLevelPoint(buildAddLevelPointRequest());
    addLevelPointCounter++;

    Integer expectedTotalCreditMutation = LEVEL_POINT_100 * addLevelPointCounter;
    Integer expectedClosingBalance = LEVEL_POINT_100 * addLevelPointCounter;

    assertEquals(ZERO_VALUE_INT, consumerProgress.getOpeningBalance());
    assertEquals(expectedClosingBalance, consumerProgress.getClosingBalance());
    assertEquals(ZERO_VALUE_INT, consumerProgress.getTotalDebitMutation());
    assertEquals(expectedTotalCreditMutation, consumerProgress.getTotalCreditMutation());
    assertEquals(expectedNextLevel, consumerProgress.getCurrentLevel());
  }

  private TierSystemAddLevelPointRequest buildAddLevelPointRequest() {
    TierSystemAddLevelPointRequest addLevelPointRequest = new TierSystemAddLevelPointRequest();
    addLevelPointRequest.setPsId(PSID);
    addLevelPointRequest.setClientId(client.getClientId());
    addLevelPointRequest.setHash(HASH);
    addLevelPointRequest.setLevelPoint(LEVEL_POINT_100);
    addLevelPointRequest.setActivity(REQUEST_ACTIVITY);
    addLevelPointRequest.setActivityObject(REQUEST_ACTIVITY_OBJECT);
    addLevelPointRequest.setActivityObjectId(REQUEST_ACTIVITY_OBJECT_ID);
    addLevelPointRequest.setAdditionalData(REQUEST_ADDITIONAL_DATA);

    return addLevelPointRequest;
  }
}
