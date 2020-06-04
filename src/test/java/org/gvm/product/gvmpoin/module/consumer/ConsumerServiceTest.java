package org.gvm.product.gvmpoin.module.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.client.ClientConsumerRepository;
import org.gvm.product.gvmpoin.module.client.ClientIds;
import org.gvm.product.gvmpoin.module.client.ClientRepository;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.exception.HashNotValidException;
import org.gvm.product.gvmpoin.module.common.exception.PsIdNotFoundException;
import org.gvm.product.gvmpoin.module.consumer.exception.ConsumerEmailVerificationException;
import org.gvm.product.gvmpoin.module.consumer.exception.EmailDoesntMatchException;
import org.gvm.product.gvmpoin.module.consumer.exception.NumericPasswordException;
import org.gvm.product.gvmpoin.module.consumer.exception.PasswordException;
import org.gvm.product.gvmpoin.module.consumer.wishlist.ConsumerWishListRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.partner.PartnerRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopularPartnerRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopularRepository;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalance;
import org.gvm.product.gvmpoin.util.DateUtil;
import org.gvm.product.gvmpoin.util.EmailBlaster;
import org.gvm.product.gvmpoin.util.EmailTemplateGenerator;
import org.gvm.product.gvmpoin.util.HashUtil;
import org.gvm.product.gvmpoin.util.MyPasswordEncoder;
import org.gvm.product.gvmpoin.util.SecurityUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HashUtil.class)
public class ConsumerServiceTest {

  @Mock
  private ConsumerRepository consumerRepository;

  @Mock
  private PartnerRepository partnerRepository;

  @Mock
  private RewardPopularRepository rewardPopularRepository;

  @Mock
  private RewardPopularPartnerRepository rewardPopularPartnerRepository;

  @Mock
  private MyPasswordEncoder myPasswordEncoder;

  @Mock
  private ClientRepository clientRepository;

  @Mock
  private Md5PasswordEncoder md5PasswordEncoder;

  @Mock
  private ClientConsumerRepository clientConsumerRepository;

  @Mock
  private RewardRepository rewardRepository;

  @Mock
  private ConsumerWishListRepository consumerWishListRepository;

  @Mock
  private SecurityUtil securityUtil;

  @Mock
  private UserDetailsService userDetailsService;

  @Mock
  private UserDetails userDetails;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private ConsumerActivityRepository consumerActivityRepository;

  @Mock
  private EmailTemplateGenerator emailTemplateGenerator;

  @Mock
  private EmailBlaster emailBlaster;

  @Mock
  private HttpServletRequest request;

  @Mock
  private Principal principal;

  @InjectMocks
  private ConsumerService consumerService;

  private static String PS_ID;
  private static String RAW_PASSWORD;
  private static String NEW_PASSWORD;
  private static String INVALID_PASSWORD;
  private static String INVALID_PASSWORD_BELOW_6_NUMERIC;
  private static String HASH_PASSWORD;
  private static String OLD_EMAIL;
  private static String NEW_EMAIL;
  private static String NEW_NAME;
  private static String OLD_NAME;
  private static String PASSWORD_PATTERN;
  private static String CLIENT_ID;
  private static String HASH_CODE;
  private static String EMAIL_VERIFICATION_CODE;
  private static Integer OPENING_BALANCE;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    PowerMockito.mockStatic(HashUtil.class);
    PS_ID = "685378368911";
    RAW_PASSWORD = "511501";
    NEW_PASSWORD = "123456";
    INVALID_PASSWORD = "123456SoSWE";
    INVALID_PASSWORD_BELOW_6_NUMERIC = "123";
    OLD_EMAIL = "chicken.butt@gmail.com";
    NEW_EMAIL = "chicken.butt2@gmail.com";
    HASH_PASSWORD = "$2a$10$KbC29elcpWgEDYL6gkHqE.3O.md27R1euSf8dq4GNwgPvwcQ.fd.C";
    OLD_NAME = "chicken";
    NEW_NAME = "VINA";
    PASSWORD_PATTERN = "(\\d{6})";
    CLIENT_ID = "womantalk";
    HASH_CODE = "31429eafabed4b3a90f8554d7a0a2db0";
    EMAIL_VERIFICATION_CODE = "Email verification code";
    OPENING_BALANCE = 50;
  }

  @Test
  public void testGetProfileByEmail() {

    final String email = "sofian.hadi@yahuu.com";
    final Client client = new Client();
    client.setId(1L);
    client.setClientId("womantalk");
    client.setClientSecret("abcdef");
    client.setWebServerRedirectUri("womantalk.com");
    client.setEmail("admin@womantalk.com");
    client.setRegistrationTime(new Date());
    client.setApproved(false);

    final TrialBalance trialBalance = new TrialBalance();
    trialBalance.setId(1L);
    trialBalance.setOpeningBalance(0);
    trialBalance.setTotalDebits(0);
    trialBalance.setTotalCredits(0);
    trialBalance.setClosingBalance(0);
    trialBalance.setLastUpdatedTime(new Date());

    final Consumer consumer = new Consumer();
    consumer.setId(1L);
    consumer.setPsId("123456789012");
    consumer.setPassword("123456");
    consumer.setEmail("abc@cde.com");
    consumer.setHashCode("xyzabc");
    consumer.setStatus(1);
    consumer.setRegisterFrom(client);
    consumer.setTrialBalance(trialBalance);
    consumer.setRegisterTime(new Date());

    doReturn(Optional.of(consumer)).when(consumerRepository).findOneByEmail(email);

    final Consumer result = consumerService.getProfileByEmail(email);

    verify(consumerRepository).findOneByEmail(eq(email));

    assertThat(result).isEqualTo(consumer);
  }

  @Test
  @PrepareForTest({MyPasswordEncoder.class, HashUtil.class})
  public void testIsPinEqualsToDatabase() {
    PowerMockito.mockStatic(MyPasswordEncoder.class);

    when(consumerRepository.findTemporaryPasswordByPsId(PS_ID))
        .thenReturn(generateOptionalConsumer(HASH_PASSWORD));
    when(myPasswordEncoder.matches(RAW_PASSWORD, generateOptionalConsumer(HASH_PASSWORD).get()
        .getTemporaryPassword())).thenReturn(true);

    boolean isEquals = consumerService.isPasswordEqualsToDatabase(PS_ID, RAW_PASSWORD);
    Assert.assertTrue(isEquals);
  }

  @Test
  @PrepareForTest({MyPasswordEncoder.class, HashUtil.class})
  public void testIsNotPinEqualsToDatabase() {
    PowerMockito.mockStatic(MyPasswordEncoder.class);

    when(consumerRepository.findTemporaryPasswordByPsId(PS_ID))
        .thenReturn(generateOptionalConsumer(HASH_PASSWORD));
    when(myPasswordEncoder
        .matches(RAW_PASSWORD, generateOptionalConsumer(HASH_PASSWORD).get()
            .getTemporaryPassword())).thenReturn(true);

    boolean isEquals = consumerService.isPasswordEqualsToDatabase(PS_ID, INVALID_PASSWORD);
    Assert.assertFalse(isEquals);
  }

  private Optional<Consumer> generateOptionalConsumer(String hashTemporaryPassword) {
    Consumer consumer = new Consumer();
    consumer.setPsId(PS_ID);
    consumer.setEmail(OLD_EMAIL);
    consumer.setEmailVerified(true);
    consumer.setPassword(HASH_PASSWORD);
    consumer.setTemporaryPassword(hashTemporaryPassword);

    Date date = new Date(System.currentTimeMillis() - (4 * 60 * 60 * 1000));

    consumer.setEmailVerificationCodeCreatedTime(date);

    Client client = new Client();
    client.setId(2L);
    consumer.setRegisterFrom(client);
    return Optional.of(consumer);
  }

  private Optional<Consumer> generateOptionalConsumerWithoutEmail(String hashTemporaryPassword) {
    Consumer consumer = new Consumer();
    consumer.setEmailVerified(false);
    consumer.setPassword(RAW_PASSWORD);
    consumer.setTemporaryPassword(hashTemporaryPassword);
    consumer.setEmailVerificationCode(EMAIL_VERIFICATION_CODE);
    consumer.setEmailVerificationCodeCreatedTime(DateUtil.getTimeByAddDays(100));
    consumer.setEmail(null);

    Client client = new Client();
    client.setId(4L);
    consumer.setRegisterFrom(client);
    return Optional.of(consumer);
  }

  private Optional<Consumer> generateOptionalConsumerWithoutTemporaryPassword(
      String hashTemporaryPassword) {
    Consumer consumer = new Consumer();
    consumer.setEmailVerified(true);
    consumer.setPassword(null);
    consumer.setTemporaryPassword(hashTemporaryPassword);
    consumer.setEmail(OLD_EMAIL);
    return Optional.of(consumer);
  }

  private Optional<Consumer> generateOptionalConsumerWithoutPassword() {
    Consumer consumer = new Consumer();
    consumer.setEmailVerified(true);
    consumer.setPassword(null);
    consumer.setTemporaryPassword(null);
    consumer.setEmail(OLD_EMAIL);
    return Optional.of(consumer);
  }

  private Optional<Client> generateOptionalClientById(String clientId) {
    Client client = new Client();
    client.setClientId(clientId);

    return Optional.of(client);
  }

  @Test(expected = PasswordException.class)
  public void testIsPinEqualsToDatabaseWhenConsumerAlreadyChangePIN() {
    when(consumerRepository.findTemporaryPasswordByPsId(PS_ID))
        .thenReturn(generateOptionalConsumer(null));
    consumerService.isPasswordEqualsToDatabase(PS_ID, NEW_PASSWORD);
  }

  @Test
  @PrepareForTest({MyPasswordEncoder.class, UsernamePasswordAuthenticationToken.class,
      HashUtil.class})
  public void testChangePin() {

    PowerMockito.mockStatic(MyPasswordEncoder.class);
    PowerMockito.mockStatic(UsernamePasswordAuthenticationToken.class);

    testIsPinEqualsToDatabase();

    Optional<Consumer> consumerOptional = generateOptionalConsumer(HASH_PASSWORD);

    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(consumerOptional);

    Assert.assertTrue(consumerOptional.get().getEmailVerified());

    if (NEW_PASSWORD.matches(PASSWORD_PATTERN)) {

      when(myPasswordEncoder.encode(NEW_PASSWORD)).thenReturn(consumerOptional.get().getPassword());

      when(userDetailsService.loadUserByUsername(PS_ID)).thenReturn(userDetails);

      when((new UsernamePasswordAuthenticationToken(userDetails, NEW_PASSWORD,
          userDetails.getAuthorities())))
          .thenReturn(any());
    }
    consumerService.changePasswordAfterFirstVerification(PS_ID, RAW_PASSWORD, NEW_PASSWORD);
  }

  @Test(expected = PasswordException.class)
  public void testChangePinWhenTemporaryPasswordDoesntMatch() {
    when(consumerRepository.findTemporaryPasswordByPsId(PS_ID))
        .thenReturn(generateOptionalConsumer(HASH_PASSWORD));
    when(myPasswordEncoder
        .matches(RAW_PASSWORD, generateOptionalConsumer(HASH_PASSWORD).get()
            .getTemporaryPassword())).thenReturn(false);
    consumerService.changePasswordAfterFirstVerification(PS_ID, RAW_PASSWORD, NEW_PASSWORD);
  }

  @Test(expected = PasswordException.class)
  @PrepareForTest({MyPasswordEncoder.class, HashUtil.class})
  public void testChangePinWhenPasswordAndPatternDoesntMatch() {

    testIsPinEqualsToDatabase();

    Optional<Consumer> consumerOptional = generateOptionalConsumer(HASH_PASSWORD);

    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(consumerOptional);

    consumerService.changePasswordAfterFirstVerification(PS_ID, RAW_PASSWORD, INVALID_PASSWORD);
  }


  @Test(expected = PasswordException.class)
  @PrepareForTest({MyPasswordEncoder.class, HashUtil.class})
  public void testChangePinWhenEmailNotVerified() {

    testIsPinEqualsToDatabase();

    Optional<Consumer> consumerOptional = generateOptionalConsumerWithoutEmail(HASH_PASSWORD);

    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(consumerOptional);

    consumerService.changePasswordAfterFirstVerification(PS_ID, RAW_PASSWORD, INVALID_PASSWORD);
  }

  @Test
  public void testUpdateProfileUserWhenEmailChanged() {
    Optional<Consumer> optConsumer = generateOptionalConsumer(HASH_PASSWORD);
    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(optConsumer);

    when(consumerRepository.saveAndFlush(optConsumer.get()))
        .thenReturn(generateConsumerAfterChangeProfile());
    Consumer updatedConsumerProfile = consumerService
        .getUpdatedConsumer(PS_ID, NEW_NAME, NEW_EMAIL);

    Assert.assertEquals(updatedConsumerProfile.getEmail(), NEW_EMAIL);
  }

  @Test
  public void testUpdateProfileUserWhenEmailParameterIsNull() {
    Optional<Consumer> optConsumer = generateOptionalConsumer(HASH_PASSWORD);

    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(optConsumer);

    when(consumerRepository.saveAndFlush(optConsumer.get()))
        .thenReturn(generateConsumerAfterChangeProfileWithoutEmail());

    consumerService.getUpdatedConsumer(PS_ID, NEW_NAME, null);
  }

  @Test
  public void testUpdateProfileUserWhenEmailNotChanged() {
    Optional<Consumer> optConsumer = generateOptionalConsumer(HASH_PASSWORD);
    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(optConsumer);

    when(consumerRepository.saveAndFlush(optConsumer.get()))
        .thenReturn(generateConsumerAfterChangeProfileWhenEmailNotChanged());
    Consumer updatedConsumerProfile = consumerService
        .getUpdatedConsumer(PS_ID, NEW_NAME, OLD_EMAIL);

    Assert.assertEquals(updatedConsumerProfile.getEmail(), OLD_EMAIL);
  }

  @Test
  public void testUpdateProfileUserWhenEmailIsNull() {
    Optional<Consumer> optConsumer = generateOptionalConsumerWithoutEmail(HASH_PASSWORD);
    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(optConsumer);

    when(consumerRepository.saveAndFlush(optConsumer.get()))
        .thenReturn(generateConsumerAfterChangeProfile());
    Consumer updatedConsumerProfile = consumerService
        .getUpdatedConsumer(PS_ID, NEW_NAME, NEW_EMAIL);

    Assert.assertEquals(updatedConsumerProfile.getEmail(), NEW_EMAIL);
  }

  @Test(expected = PsIdNotFoundException.class)
  public void testUpdateProfileUserWhenPsIdNotFound() {
    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(Optional.empty());

    consumerService.getUpdatedConsumer(PS_ID, NEW_NAME, NEW_EMAIL);
  }

  private Consumer generateConsumerAfterChangeProfile() {
    Consumer consumer = new Consumer();
    consumer.setEmail(NEW_EMAIL);
    consumer.setName(NEW_NAME);
    consumer.setEmailVerified(false);

    return consumer;
  }

  private Consumer generateConsumerAfterChangeProfileWhenEmailNotChanged() {
    Consumer consumer = new Consumer();
    consumer.setEmail(OLD_EMAIL);
    consumer.setName(NEW_NAME);
    consumer.setEmailVerified(false);

    return consumer;
  }

  private Consumer generateConsumerAfterChangeProfileWithoutEmail() {
    Consumer consumer = new Consumer();
    consumer.setEmail(null);
    consumer.setName(NEW_NAME);
    consumer.setEmailVerified(false);

    return consumer;
  }

  private User buildUserFromConsumer() {
    return new User(
        PS_ID,
        HASH_PASSWORD,
        true,
        true,
        true,
        true,
        getAuthorities("1"));
  }

  private Collection<GrantedAuthority> getAuthorities(String role) {
    List<GrantedAuthority> authList = new ArrayList<>(1);
    authList.add(new SimpleGrantedAuthority(role));

    return authList;
  }

  @Test
  @Ignore
  public void testGenerateNewPinByPsId() {
    PowerMockito.mockStatic(HashUtil.class);

    Optional<Consumer> optConsumer = generateOptionalConsumer(HASH_PASSWORD);
    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(optConsumer);

    boolean client = optConsumer.get().getRegisterFrom().getId() == ClientIds.WOMANTALK.getValue();

    Assert.assertSame(client, true);

//    emailTemplateGenerator.generateEmailNewPin("", "", optConsumer.get(), "");
    emailBlaster.send(optConsumer.get().getEmail(), "", "");

    consumerService.generateNewPasswordByPsId(PS_ID, ConsumerLoginStatus.DOESNT_HAS_PIN.getValue());
  }

  @Test
  @PrepareForTest({SecurityUtil.class, HashUtil.class})
  public void testGetProfileByPsId() {
    PowerMockito.mockStatic(SecurityUtil.class);

    Optional<Consumer> expectedConsumer = generateOptionalConsumer(HASH_PASSWORD);
    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(expectedConsumer);

    Optional<Consumer> actualConsumer = generateOptionalConsumer(HASH_PASSWORD);

    Assert.assertTrue(actualConsumer.isPresent());

    actualConsumer.get().setHash(securityUtil.getHashForPsId(PS_ID));

    consumerService.getProfileByPsId(PS_ID);

  }

  @Test(expected = PsIdNotFoundException.class)
  @PrepareForTest({PsIdNotFoundException.class, SecurityUtil.class, HashUtil.class})
  public void testGetProfileByPsIdWhenNotExists() {
    PowerMockito.mockStatic(SecurityUtil.class);

    Optional<Consumer> expectedConsumer = Optional.empty();

    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(expectedConsumer);

    Consumer actualConsumer = consumerService.getProfileByPsId(PS_ID);

    Assert.assertNull(actualConsumer);
  }

  @Test
  @Ignore
  @PrepareForTest(HashUtil.class)
  public void testForgotPasswordPasswordByPsId() {
    PowerMockito.mockStatic(HashUtil.class);

    Optional<Consumer> optConsumer = generateOptionalConsumer(HASH_PASSWORD);
    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(optConsumer);

    // when(HashUtil.generateNewPassword()).thenReturn(any());

    optConsumer.get().getRegisterFrom().setClientId(CLIENT_ID);

    consumerService.generateNewPasswordByPsId(PS_ID, ConsumerLoginStatus.DOESNT_HAS_PIN.getValue());
  }

  @Test
  public void testGetConsumerStatusWhenEmailNotVerified() {
    Optional<Consumer> optConsumer = generateOptionalConsumerWithoutEmail(HASH_PASSWORD);

    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(optConsumer);

    Integer status = consumerService.getConsumerStatus(PS_ID, request, principal);

    Assert.assertSame(status, ConsumerLoginStatus.EMAIL_NOT_VERIFIED.getValue());
  }

  @Test
  public void testGetConsumerStatusWhenForgotPin() {
    Optional<Consumer> optConsumer = generateOptionalConsumerWithoutTemporaryPassword(
        HASH_PASSWORD);

    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(optConsumer);

    Integer status = consumerService.getConsumerStatus(PS_ID, request, principal);

    Assert.assertSame(status, ConsumerLoginStatus.FORGOT_PIN.getValue());
  }

  @Test
  public void testGetConsumerStatusWhenDoesntHasPin() {
    Optional<Consumer> optConsumer = generateOptionalConsumerWithoutPassword();

    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(optConsumer);

    Integer status = consumerService.getConsumerStatus(PS_ID, request, principal);

    Assert.assertSame(status, ConsumerLoginStatus.DOESNT_HAS_PIN.getValue());
  }

  @Test
  public void testGetConsumerWhenPinAuthenticated() {
    Optional<Consumer> optConsumer = generateOptionalConsumer(HASH_PASSWORD);

    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(optConsumer);

    when(myPasswordEncoder.matches(RAW_PASSWORD, optConsumer.get().getPassword())).thenReturn(true);

    consumerService.getConsumerWhenPinAuthenticated(PS_ID, RAW_PASSWORD);
  }

  @Test(expected = PasswordException.class)
  @PrepareForTest({PasswordException.class, HashUtil.class})
  public void testGetConsumerWhenPinAuthenticatedNotMatched() {
    Optional<Consumer> optConsumer = generateOptionalConsumer(HASH_PASSWORD);
    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(optConsumer);
    when(myPasswordEncoder.matches(RAW_PASSWORD, optConsumer.get().getPassword()))
        .thenReturn(false);

    consumerService.getConsumerWhenPinAuthenticated(PS_ID, RAW_PASSWORD);
  }

  @Test
  public void testEmailBlastAdminWhenPsIdIsNullFromClient() {

    emailBlaster.sendMultipleRecipients(OLD_EMAIL, "PS ID NULL",
        "Identity has not PIN");

    consumerService.emailBlastAdminWhenPsIdIsNullFromClient(OLD_EMAIL);
  }

  @Test(expected = HashNotValidException.class)
  public void testSetEmailVerifiedWhenPasswordInvalid() {
    Optional<Client> optClient = generateOptionalClientById(CLIENT_ID);
    when(clientRepository.findOneByClientId(CLIENT_ID)).thenReturn(optClient);

    boolean matchHash = md5PasswordEncoder.isPasswordValid(HASH_PASSWORD, PS_ID, Constant.SALT);

    Assert.assertSame(matchHash, false);

    consumerService
        .getDetailAfterEmailVerificationHandling(CLIENT_ID, OLD_EMAIL, PS_ID, HASH_PASSWORD);
  }

  @Test
  public void testSetEmailVerifiedWhenPasswordValid() {
    Optional<Client> optClient = generateOptionalClientById(CLIENT_ID);
    when(clientRepository.findOneByClientId(CLIENT_ID)).thenReturn(optClient);

    when(md5PasswordEncoder.isPasswordValid(HASH_CODE, PS_ID, Constant.SALT)).thenReturn(true);

    Optional<Consumer> optConsumer = generateOptionalConsumer(HASH_PASSWORD);
    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(optConsumer);

    consumerService.getDetailAfterEmailVerificationHandling(CLIENT_ID, OLD_EMAIL, PS_ID, HASH_CODE);
  }

  @Test(expected = EmailDoesntMatchException.class)
  public void testSetEmailVerifiedWhenEmailNotEqual() {
    Optional<Client> optClient = generateOptionalClientById(CLIENT_ID);
    when(clientRepository.findOneByClientId(CLIENT_ID)).thenReturn(optClient);

    when(md5PasswordEncoder.isPasswordValid(HASH_CODE, PS_ID, Constant.SALT)).thenReturn(true);

    Optional<Consumer> optConsumer = generateOptionalConsumer(HASH_PASSWORD);
    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(optConsumer);

    consumerService.getDetailAfterEmailVerificationHandling(CLIENT_ID, NEW_EMAIL, PS_ID, HASH_CODE);
  }

  @Test
  public void testEmailVerification() {
    Optional<Consumer> optConsumer = generateOptionalConsumer(HASH_PASSWORD);
    when(consumerRepository.findOneByEmailVerificationCode(EMAIL_VERIFICATION_CODE))
        .thenReturn(optConsumer);

    consumerService.emailVerification(EMAIL_VERIFICATION_CODE);
  }

  @Test(expected = ConsumerEmailVerificationException.class)
  public void testExpiredEmailVerification() {
    Optional<Consumer> optConsumer = generateOptionalConsumerWithoutEmail(HASH_PASSWORD);
    when(consumerRepository.findOneByEmailVerificationCode(EMAIL_VERIFICATION_CODE))
        .thenReturn(optConsumer);

    consumerService.emailVerification(EMAIL_VERIFICATION_CODE);
  }

  @Test(expected = PsIdNotFoundException.class)
  public void testChangePasswordByWrongPsId() {

    when(consumerRepository.findOneByPsId(PS_ID)).thenReturn(Optional.empty());

    consumerService.changePassword(PS_ID, RAW_PASSWORD, NEW_PASSWORD, request);

  }

  @Test(expected = PasswordException.class)
  public void testChangePasswordWhenOldAndNewPasswordDoesNotMatch() {

    when(consumerRepository.findOneByPsId(PS_ID))
        .thenReturn(generateOptionalConsumer(HASH_PASSWORD));

    when(myPasswordEncoder.matches(INVALID_PASSWORD, generateOptionalConsumer(HASH_PASSWORD).get()
        .getPassword())).thenReturn(false);

    consumerService.changePassword(PS_ID, INVALID_PASSWORD, NEW_PASSWORD, request);

  }

  @Test(expected = NumericPasswordException.class)
  public void testChangePasswordWhenNewPasswordDoesNotNumericType() {

    when(consumerRepository.findOneByPsId(PS_ID))
        .thenReturn(generateOptionalConsumer(HASH_PASSWORD));

    when(myPasswordEncoder.matches(RAW_PASSWORD, generateOptionalConsumer(HASH_PASSWORD).get()
        .getPassword())).thenReturn(true);

    consumerService.changePassword(PS_ID, RAW_PASSWORD, INVALID_PASSWORD, request);

  }

  @Test(expected = NumericPasswordException.class)
  public void testChangePasswordWhenNewPasswordBelowThreeNumericCharacter() {

    when(consumerRepository.findOneByPsId(PS_ID))
        .thenReturn(generateOptionalConsumer(HASH_PASSWORD));

    when(myPasswordEncoder.matches(RAW_PASSWORD, generateOptionalConsumer(HASH_PASSWORD).get()
        .getPassword())).thenReturn(true);

    consumerService.changePassword(PS_ID, RAW_PASSWORD, INVALID_PASSWORD_BELOW_6_NUMERIC, request);

  }

  @Test
  public void testChangePassword() {

    when(consumerRepository.findOneByPsId(PS_ID))
        .thenReturn(generateOptionalConsumer(HASH_PASSWORD));

    when(myPasswordEncoder.matches(RAW_PASSWORD, generateOptionalConsumer(HASH_PASSWORD).get()
        .getPassword())).thenReturn(true);

    when(myPasswordEncoder.encode(NEW_PASSWORD)).thenReturn(HASH_PASSWORD);

    consumerService.changePassword(PS_ID, RAW_PASSWORD, NEW_PASSWORD, request);

    verify(myPasswordEncoder).encode(NEW_PASSWORD);
//    verify(consumerRepository)
//        .saveAndFlush(generateOptionalConsumer(HASH_PASSWORD).get());

  }
}