package org.gvm.product.gvmpoin.module.trialbalance;

import java.util.Date;
import java.util.Optional;
import org.gvm.product.gvmpoin.module.campaign.CampaignService;
import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.client.ClientConsumerRepository;
import org.gvm.product.gvmpoin.module.client.ClientRepository;
import org.gvm.product.gvmpoin.module.client.exception.ClientNotFoundException;
import org.gvm.product.gvmpoin.module.common.exception.ExceededBalanceException;
import org.gvm.product.gvmpoin.module.common.exception.HashNotValidException;
import org.gvm.product.gvmpoin.module.common.exception.NegativeNumberException;
import org.gvm.product.gvmpoin.module.common.exception.PsIdNotFoundException;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerRepository;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntry;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntryRepository;
import org.gvm.product.gvmpoin.module.tiersystem.TierSystemService;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceParam.Builder;
import org.gvm.product.gvmpoin.util.EmailBlaster;
import org.gvm.product.gvmpoin.util.MyPasswordEncoder;
import org.gvm.product.gvmpoin.util.SecurityUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
// @SpringApplicationConfiguration(classes = Launcher.class)
public class TrialBalanceServiceTest {

  private static Client client;
  private static Consumer consumer;
  private static TrialBalance trialBalance;
  private static String salt = "Aha9yeWq4WP3zGh0eKW5l2wehwz/XRCf8CijBKcqLbNZ";

  private static TrialBalanceRepository trialBalanceRepository;
  private static JournalEntryRepository journalEntryRepository;
  private static ConsumerRepository consumerRepository;
  private static Md5PasswordEncoder md5PasswordEncoder;
  private static ClientRepository clientRepository;
  private static CampaignService campaignService;
  private static TierSystemService tierSystemService;
  private static ClientConsumerRepository clientConsumerRepository;
  private static EmailBlaster emailBlaster;
  private static SecurityUtil securityUtil;
  private static MyPasswordEncoder myPasswordEncoder;

  @BeforeClass
  public static void setUpForInitialization() throws Exception {
    client = new Client();
    client.setId(1L);
    client.setClientId("womantalk");
    client.setApproved(true);
    client.setClientSecret("womantalk_secret");
    client.setEmail("admin@womantalk.com");
    client.setRegistrationTime(new Date());
    client.setWebServerRedirectUri("womantalk.com");

    trialBalanceRepository = mock(TrialBalanceRepository.class);
    journalEntryRepository = mock(JournalEntryRepository.class);
    consumerRepository = mock(ConsumerRepository.class);
    md5PasswordEncoder = mock(Md5PasswordEncoder.class);
    clientRepository = mock(ClientRepository.class);
    tierSystemService = mock(TierSystemService.class);
    clientConsumerRepository = mock(ClientConsumerRepository.class);
    emailBlaster = mock(EmailBlaster.class);
    securityUtil = mock(SecurityUtil.class);
    myPasswordEncoder = mock(MyPasswordEncoder.class);

    when(clientRepository.findOneByClientId("womantalk")).thenReturn(Optional.of(client));
    when(clientRepository.findOneByClientId("invalidClient"))
        .thenThrow(new ClientNotFoundException("invalidClient")); // yg dithrow khusus tipe runtime
    // hrs di beforeclass
    when(consumerRepository.findOneByPsId("12345678901211"))
        .thenThrow(new PsIdNotFoundException("12345678901211"));
    when(md5PasswordEncoder.isPasswordValid("qwerty", "123456789012", salt)).thenReturn(true);
    when(md5PasswordEncoder.isPasswordValid("nonqwerty", "123456789012", salt))
        .thenThrow(new HashNotValidException("nonqwerty"));
  }

  @Before
  public void setupBeforeEachTest() {
    trialBalance = new TrialBalance();
    trialBalance.setId(1L);
    trialBalance.setOpeningBalance(10000);
    trialBalance.setTotalDebits(0);
    trialBalance.setTotalCredits(0);
    trialBalance.setClosingBalance(10000);

    consumer = new Consumer();
    consumer.setPsId("123456789012");
    consumer.setEmail("sofian@abc.com");
    consumer.setRegisterFrom(client);
    consumer.setTrialBalance(trialBalance);
    consumer.setStatus(1);
    consumer.setPassword("qwerty");
    consumer.setName("sofian");

    when(consumerRepository.findOneByPsId("123456789012")).thenReturn(Optional.of(consumer));
    when(trialBalanceRepository.findTrialBalanceForUpdate(1L))
        .thenReturn(Optional.of(trialBalance));
  }

  @Test
  public void testCredit() {
    TrialBalanceService trialBalanceService = new TrialBalanceService(
        trialBalanceRepository, journalEntryRepository, consumerRepository, clientRepository,
        campaignService, tierSystemService, clientConsumerRepository, emailBlaster, securityUtil,
        myPasswordEncoder);
    final String clientId = "womantalk";
    final String md5Hashed = "qwerty";
    final String psId = "123456789012";
    final int amount = 1000;
    final String description = "topup";
    final String activity = "SHARE";
    final String objectActivity = "ARTICLE";
    final Long objectId = 50L;
    final String additionalData = "ADDENDUM";

    JournalEntry internalJournal = new JournalEntry();
    internalJournal.setId(1L);
    internalJournal.setClientId(clientId);
    internalJournal.setDebit(0);
    internalJournal.setCredit(amount);
    internalJournal.setDescription(description);
    internalJournal.setTransactionTime(new Date());
    internalJournal.setTrialBalance(trialBalance);
    internalJournal.setActivity(activity);
    internalJournal.setActivityObject(objectActivity);
    internalJournal.setObjectId(objectId);
    internalJournal.setAdditionalData(additionalData);

    // add behavior
    when(journalEntryRepository.saveAndFlush(Mockito.any(JournalEntry.class)))
        .thenReturn(internalJournal);

    Builder builder = new TrialBalanceParam.Builder(psId, md5Hashed, amount, activity,
        objectActivity, clientId);
    final TrialBalanceParam addBalanceParam = builder.description(description)
        .objectId(objectId)
        .additionalData(additionalData)
        .build();
    JournalEntry result =
        trialBalanceService.credit(addBalanceParam);

    // test
    assertThat(result.getCredit()).isEqualTo(amount);
    assertThat(result.getTrialBalance().getClosingBalance()).isEqualTo(11000);
  }

  //  @Test(expected = ClientNotFoundException.class)
  @Ignore
  public void testCreditWithIllegalClient() {
    TrialBalanceService trialBalanceService = new TrialBalanceService(
        trialBalanceRepository, journalEntryRepository, consumerRepository, clientRepository,
        campaignService, tierSystemService, clientConsumerRepository, emailBlaster, securityUtil,
        myPasswordEncoder);

    final String clientId = "invalidClient";
    final String md5Hashed = "qwerty";
    final String psId = "123456789012";
    final int amount = 1000;
    final String description = "topup";
    final String activity = "SHARE";
    final String objectActivity = "ARTICLE";
    final Long objectId = 50L;
    final String additionalData = "ADDENDUM";

    JournalEntry internalJournal = new JournalEntry();
    internalJournal.setId(1L);
    internalJournal.setClientId(clientId);
    internalJournal.setDebit(0);
    internalJournal.setCredit(amount);
    internalJournal.setDescription(description);
    internalJournal.setTransactionTime(new Date());
    internalJournal.setTrialBalance(trialBalance);
    internalJournal.setActivity(activity);
    internalJournal.setActivityObject(objectActivity);
    internalJournal.setObjectId(objectId);
    internalJournal.setAdditionalData(additionalData);

    // add behavior
    when(journalEntryRepository.saveAndFlush(Mockito.any(JournalEntry.class)))
        .thenReturn(internalJournal);

    Builder builder = new TrialBalanceParam.Builder(psId, md5Hashed, amount, activity,
        objectActivity, clientId);
    final TrialBalanceParam addBalanceParam = builder.description(description)
        .objectId(objectId)
        .additionalData(additionalData)
        .build();
    JournalEntry result =
        trialBalanceService.credit(addBalanceParam);

    // test
    assertThat(result.getCredit()).isEqualTo(amount);
    assertThat(result.getTrialBalance().getClosingBalance())
        .isEqualTo(trialBalance.getClosingBalance());
  }

  @Test(expected = NegativeNumberException.class)
  public void testCreditWithNegativeNumber() {
    TrialBalanceService trialBalanceService = new TrialBalanceService(
        trialBalanceRepository, journalEntryRepository, consumerRepository, clientRepository,
        campaignService, tierSystemService, clientConsumerRepository, emailBlaster, securityUtil,
        myPasswordEncoder);

    final String clientId = "womantalk";
    final String md5Hashed = "qwerty";
    final String psId = "123456789012";
    final int amount = -1000;
    final String description = "topup";
    final String activity = "SHARE";
    final String objectActivity = "ARTICLE";
    final Long objectId = 50L;
    final String additionalData = "ADDENDUM";

    JournalEntry internalJournal = new JournalEntry();
    internalJournal.setId(1L);
    internalJournal.setClientId(clientId);
    internalJournal.setDebit(0);
    internalJournal.setCredit(amount);
    internalJournal.setDescription(description);
    internalJournal.setTransactionTime(new Date());
    internalJournal.setTrialBalance(trialBalance);
    internalJournal.setActivity(activity);
    internalJournal.setActivityObject(objectActivity);
    internalJournal.setObjectId(objectId);
    internalJournal.setAdditionalData(additionalData);

    // add behavior
    when(journalEntryRepository.saveAndFlush(Mockito.any(JournalEntry.class)))
        .thenReturn(internalJournal);

    Builder builder = new TrialBalanceParam.Builder(psId, md5Hashed, amount, activity,
        objectActivity, clientId);
    final TrialBalanceParam addBalanceParam = builder.description(description)
        .objectId(objectId)
        .additionalData(additionalData)
        .build();
    JournalEntry result = trialBalanceService.credit(addBalanceParam);

    // test
    assertThat(result.getCredit()).isEqualTo(amount);
    assertThat(result.getTrialBalance().getClosingBalance())
        .isEqualTo(trialBalance.getClosingBalance());
  }

  @Test
  public void tesDebit() {
    TrialBalanceService trialBalanceService = new TrialBalanceService(
        trialBalanceRepository, journalEntryRepository, consumerRepository, clientRepository,
        campaignService, tierSystemService, clientConsumerRepository, emailBlaster, securityUtil,
        myPasswordEncoder);

    final String clientId = "womantalk";
    final String md5Hashed = "qwerty";
    final String psId = "123456789012";
    final int amount = 1000;
    final String description = "redeemn";
    final String activity = "REDEEMN";
    final String objectActivity = "REWARD";
    final Long objectId = 50L;
    final String additionalData = "ADDENDUM";

    JournalEntry internalJournal = new JournalEntry();
    internalJournal.setId(1L);
    internalJournal.setClientId(clientId);
    internalJournal.setDebit(amount);
    internalJournal.setCredit(0);
    internalJournal.setDescription(description);
    internalJournal.setTransactionTime(new Date());
    internalJournal.setTrialBalance(trialBalance);
    internalJournal.setActivity(activity);
    internalJournal.setActivityObject(objectActivity);
    internalJournal.setObjectId(objectId);
    internalJournal.setAdditionalData(additionalData);

    // add behavior
    when(journalEntryRepository.saveAndFlush(Mockito.any(JournalEntry.class)))
        .thenReturn(internalJournal);

    Builder builder = new TrialBalanceParam.Builder(psId, md5Hashed, amount, activity,
        objectActivity, clientId);
    final TrialBalanceParam substractBalanceParam = builder.description(description)
        .objectId(objectId)
        .additionalData(additionalData)
        .build();
    JournalEntry result = trialBalanceService.debit(substractBalanceParam);

    // test
    assertThat(result.getDebit()).isEqualTo(amount);
    assertThat(result.getTrialBalance().getClosingBalance()).isEqualTo(9000);
  }

  @Test
  public void tesDebitWithZeroValue() {
    TrialBalanceService trialBalanceService = new TrialBalanceService(
        trialBalanceRepository, journalEntryRepository, consumerRepository, clientRepository,
        campaignService, tierSystemService, clientConsumerRepository, emailBlaster, securityUtil,
        myPasswordEncoder);

    final String clientId = "womantalk";
    final String md5Hashed = "qwerty";
    final String psId = "123456789012";
    final int amount = 0;
    final String description = "redeemn";
    final String activity = "REDEEMN";
    final String objectActivity = "REWARD";
    final Long objectId = 50L;
    final String additionalData = "ADDENDUM";

    JournalEntry internalJournal = new JournalEntry();
    internalJournal.setId(1L);
    internalJournal.setClientId(clientId);
    internalJournal.setDebit(amount);
    internalJournal.setCredit(0);
    internalJournal.setDescription(description);
    internalJournal.setTransactionTime(new Date());
    internalJournal.setTrialBalance(trialBalance);
    internalJournal.setActivity(activity);
    internalJournal.setActivityObject(objectActivity);
    internalJournal.setObjectId(objectId);
    internalJournal.setAdditionalData(additionalData);

    // add behavior
    Builder builder = new TrialBalanceParam.Builder(psId, md5Hashed, amount, activity,
        objectActivity, clientId);
    final TrialBalanceParam substractBalanceParam = builder.description(description)
        .objectId(objectId)
        .additionalData(additionalData)
        .build();
    when(journalEntryRepository.saveAndFlush(Mockito.any(JournalEntry.class)))
        .thenReturn(internalJournal);

    JournalEntry result = trialBalanceService.debit(substractBalanceParam);

    // test
    assertThat(result.getDebit()).isEqualTo(amount);
    assertThat(result.getTrialBalance().getClosingBalance()).isEqualTo(10000);
  }

  @Test(expected = ClientNotFoundException.class)
  public void testDebitWithIllegalClient() {
    TrialBalanceService trialBalanceService = new TrialBalanceService(
        trialBalanceRepository, journalEntryRepository, consumerRepository, clientRepository,
        campaignService, tierSystemService, clientConsumerRepository, emailBlaster, securityUtil,
        myPasswordEncoder);

    final String clientId = "invalidClient";
    final String md5Hashed = "qwerty";
    final String psId = "123456789012";
    final int amount = 1000;
    final String description = "redeemn";
    final String activity = "REDEEMN";
    final String objectActivity = "REWARD";
    final Long objectId = 50L;
    final String additionalData = "ADDENDUM";

    JournalEntry internalJournal = new JournalEntry();
    internalJournal.setId(1L);
    internalJournal.setClientId(clientId);
    internalJournal.setDebit(amount);
    internalJournal.setCredit(0);
    internalJournal.setDescription(description);
    internalJournal.setTransactionTime(new Date());
    internalJournal.setTrialBalance(trialBalance);
    internalJournal.setActivity(activity);
    internalJournal.setActivityObject(objectActivity);
    internalJournal.setObjectId(objectId);
    internalJournal.setAdditionalData(additionalData);

    // add behavior
    when(journalEntryRepository.saveAndFlush(Mockito.any(JournalEntry.class)))
        .thenReturn(internalJournal);

    Builder builder = new TrialBalanceParam.Builder(psId, md5Hashed, amount, activity,
        objectActivity, clientId);
    final TrialBalanceParam substractBalanceParam = builder.description(description)
        .objectId(objectId)
        .additionalData(additionalData)
        .build();

    JournalEntry result = trialBalanceService.debit(substractBalanceParam);

    // test
    assertThat(result.getDebit()).isEqualTo(amount);
    assertThat(result.getTrialBalance().getClosingBalance())
        .isEqualTo(trialBalance.getClosingBalance());
  }

  @Test(expected = NegativeNumberException.class)
  public void testDebitWithNegativeNumber() {
    TrialBalanceService trialBalanceService = new TrialBalanceService(
        trialBalanceRepository, journalEntryRepository, consumerRepository, clientRepository,
        campaignService, tierSystemService, clientConsumerRepository, emailBlaster, securityUtil,
        myPasswordEncoder);

    final String clientId = "womantalk";
    final String md5Hashed = "qwerty";
    final String psId = "123456789012";
    final int amount = -1000;
    final String description = "redeemn";
    final String activity = "REDEEMN";
    final String objectActivity = "REWARD";
    final Long objectId = 50L;
    final String additionalData = "ADDENDUM";

    JournalEntry internalJournal = new JournalEntry();
    internalJournal.setId(1L);
    internalJournal.setClientId(clientId);
    internalJournal.setDebit(0);
    internalJournal.setCredit(amount);
    internalJournal.setDescription(description);
    internalJournal.setTransactionTime(new Date());
    internalJournal.setTrialBalance(trialBalance);
    internalJournal.setActivity(activity);
    internalJournal.setActivityObject(objectActivity);
    internalJournal.setObjectId(objectId);
    internalJournal.setAdditionalData(additionalData);

    // add behavior
    when(journalEntryRepository.saveAndFlush(Mockito.any(JournalEntry.class)))
        .thenReturn(internalJournal);

    Builder builder = new TrialBalanceParam.Builder(psId, md5Hashed, amount, activity,
        objectActivity, clientId);
    final TrialBalanceParam substractBalanceParam = builder.description(description)
        .objectId(objectId)
        .additionalData(additionalData)
        .build();

    JournalEntry result = trialBalanceService.debit(substractBalanceParam);

    // test
    assertThat(result.getCredit()).isEqualTo(amount);
    assertThat(result.getTrialBalance().getClosingBalance())
        .isEqualTo(trialBalance.getClosingBalance());
  }

  @Test(expected = ExceededBalanceException.class)
  public void testExceededBalance() {
    TrialBalanceService trialBalanceService = new TrialBalanceService(
        trialBalanceRepository, journalEntryRepository, consumerRepository, clientRepository,
        campaignService, tierSystemService, clientConsumerRepository, emailBlaster, securityUtil,
        myPasswordEncoder);

    final String clientId = "womantalk";
    final String md5Hashed = "qwerty";
    final String psId = "123456789012";
    final int amount = 11000;
    final String description = "redeemn";
    final String activity = "REDEEMN";
    final String objectActivity = "REWARD";
    final Long objectId = 50L;
    final String additionalData = "ADDENDUM";

    JournalEntry internalJournal = new JournalEntry();
    internalJournal.setId(1L);
    internalJournal.setClientId(clientId);
    internalJournal.setDebit(amount);
    internalJournal.setCredit(0);
    internalJournal.setDescription(description);
    internalJournal.setTransactionTime(new Date());
    internalJournal.setTrialBalance(trialBalance);
    internalJournal.setActivity(activity);
    internalJournal.setActivityObject(objectActivity);
    internalJournal.setObjectId(objectId);
    internalJournal.setAdditionalData(additionalData);

    // add behavior
    when(journalEntryRepository.saveAndFlush(Mockito.any(JournalEntry.class)))
        .thenReturn(internalJournal);

    Builder builder = new TrialBalanceParam.Builder(psId, md5Hashed, amount, activity,
        objectActivity, clientId);
    final TrialBalanceParam substractBalanceParam = builder.description(description)
        .objectId(objectId)
        .additionalData(additionalData)
        .build();
    trialBalanceService.debit(substractBalanceParam);
  }

  @Test
  public void testGetTrialBalance() {
    final String md5Hashed = "qwerty";
    final String psId = "123456789012";

    TrialBalanceService trialBalanceService = new TrialBalanceService(
        trialBalanceRepository, journalEntryRepository, consumerRepository, clientRepository,
        campaignService, tierSystemService, clientConsumerRepository, emailBlaster, securityUtil,
        myPasswordEncoder);

    TrialBalance result = trialBalanceService.getTrialBalance(psId, md5Hashed);
    assertThat(result.getClosingBalance()).isEqualTo(10000);
  }

  @Test(expected = PsIdNotFoundException.class)
  public void testGetTrialBalanceWithInvalidPsId() {
    final String md5Hashed = "qwerty";
    final String psId = "12345678901211";

    TrialBalanceService trialBalanceService = new TrialBalanceService(
        trialBalanceRepository, journalEntryRepository, consumerRepository, clientRepository,
        campaignService, tierSystemService, clientConsumerRepository, emailBlaster, securityUtil,
        myPasswordEncoder);

    TrialBalance result = trialBalanceService.getTrialBalance(psId, md5Hashed);
    assertThat(result.getClosingBalance()).isEqualTo(10000);
  }

  //  @Test(expected = HashNotValidException.class)
  @Ignore
  public void testGetTrialBalanceWithInvalidHash() {
    final String md5Hashed = "nonqwerty";
    final String psId = "123456789012";

    TrialBalanceService trialBalanceService = new TrialBalanceService(
        trialBalanceRepository, journalEntryRepository, consumerRepository, clientRepository,
        campaignService, tierSystemService, clientConsumerRepository, emailBlaster, securityUtil,
        myPasswordEncoder);

    TrialBalance result = trialBalanceService.getTrialBalance(psId, md5Hashed);
    assertThat(result.getClosingBalance()).isEqualTo(10000);
  }

}
