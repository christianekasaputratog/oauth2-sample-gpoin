package org.gvm.product.gvmpoin.module.consumer;

import java.sql.Timestamp;
import org.apache.commons.lang3.RandomStringUtils;
import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.client.ClientConsumerRepository;
import org.gvm.product.gvmpoin.module.client.ClientRepository;
import org.gvm.product.gvmpoin.module.client.exception.ClientNotFoundException;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.RestStatus;
import org.gvm.product.gvmpoin.module.common.exception.HashNotValidException;
import org.gvm.product.gvmpoin.module.common.exception.PsIdNotFoundException;
import org.gvm.product.gvmpoin.module.consumer.exception.ConsumerAlreadyExistException;
import org.gvm.product.gvmpoin.module.consumer.exception.ConsumerEmailVerificationException;
import org.gvm.product.gvmpoin.module.consumer.exception.EmailAlreadyExistException;
import org.gvm.product.gvmpoin.module.consumer.exception.EmailDoesntMatchException;
import org.gvm.product.gvmpoin.module.consumer.exception.EmailMustExistException;
import org.gvm.product.gvmpoin.module.consumer.exception.EmailNotFoundException;
import org.gvm.product.gvmpoin.module.consumer.exception.NumericPasswordException;
import org.gvm.product.gvmpoin.module.consumer.exception.PasswordException;
import org.gvm.product.gvmpoin.module.consumer.exception.SocialMediaException;
import org.gvm.product.gvmpoin.module.consumer.socialmedia.ConsumerSocialMedia;
import org.gvm.product.gvmpoin.module.consumer.socialmedia.ConsumerSocialMediaRepository;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalance;
import org.gvm.product.gvmpoin.util.DateUtil;
import org.gvm.product.gvmpoin.util.EmailBlaster;
import org.gvm.product.gvmpoin.util.EmailTemplateGenerator;
import org.gvm.product.gvmpoin.util.HashUtil;
import org.gvm.product.gvmpoin.util.MyPasswordEncoder;
import org.gvm.product.gvmpoin.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.security.Principal;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class ConsumerService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private static final String DEFAULT_CLIENT_ID = "gvm-poin";
  private static final int EMAIL_VERIFICATION_CODE_EXPIRY_IN_HOURS = 24;
  private static final String PASSWORD_PATTERN = "(\\d{6})";

  private ConsumerRepository consumerRepository;
  private MyPasswordEncoder myPasswordEncoder;
  private ClientRepository clientRepository;
  private Md5PasswordEncoder md5PasswordEncoder;
  private EmailTemplateGenerator emailTemplateGenerator;
  private ClientConsumerRepository clientConsumerRepository;
  private SecurityUtil securityUtil;
  private ConsumerActivityRepository consumerActivityRepository;
  private AuthenticationManager authenticationManager;
  private UserDetailsService userDetailsService;
  private EmailBlaster emailBlaster;
  private ConsumerSocialMediaRepository consumerSocialMediaRepository;

  @Value("${gpoin-email-generatepin.subject}")
  private String gpoinEmailGeneratePinSubject;

  @Value("${gpoin-email-resetpin.subject}")
  private String gpoinEmailResetPinSubject;

  /**
   * Bean Configuration for Consumer Service .
   *
   * @param consumerRepository Consumer Interface
   * @param myPasswordEncoder My Password Encoder Implementation
   * @param clientRepository Client Interface
   * @param md5PasswordEncoder MD5 Password Encoder Implementation
   * @param clientConsumerRepository Client Consumer Interface
   * @param emailBlaster Email Blaster Implementation
   * @param authenticationManager Authentication Manager Implementation
   * @param securityUtil Security Util
   * @param userDetailsService User Details Implementation
   * @param consumerActivityRepository Consumer Activity Interface
   * @param consumerSocialMediaRepository Consumer Social Media Interface
   */
  @Autowired
  public ConsumerService(ConsumerRepository consumerRepository,
      MyPasswordEncoder myPasswordEncoder, EmailTemplateGenerator emailTemplateGenerator,
      ClientRepository clientRepository, Md5PasswordEncoder md5PasswordEncoder,
      ClientConsumerRepository clientConsumerRepository,
      SecurityUtil securityUtil, ConsumerActivityRepository consumerActivityRepository,
      EmailBlaster emailBlaster, AuthenticationManager authenticationManager,
      UserDetailsService userDetailsService,
      ConsumerSocialMediaRepository consumerSocialMediaRepository) {
    this.emailBlaster = emailBlaster;
    this.userDetailsService = userDetailsService;
    this.authenticationManager = authenticationManager;
    this.consumerRepository = consumerRepository;
    this.myPasswordEncoder = myPasswordEncoder;
    this.clientRepository = clientRepository;
    this.md5PasswordEncoder = md5PasswordEncoder;
    this.clientConsumerRepository = clientConsumerRepository;
    this.securityUtil = securityUtil;
    this.consumerActivityRepository = consumerActivityRepository;
    this.emailTemplateGenerator = emailTemplateGenerator;
    this.consumerSocialMediaRepository = consumerSocialMediaRepository;
  }

  public String getExistedProfileResponseByEmail(String email) {
//    Optional<Consumer> consumer = consumerRepository.findOneByEmail(email);
//    if (consumer.isPresent()) {
//      return "Email is Exists";
//    } else {
    throw new EmailNotFoundException(email);
//    }
  }

  /**
   * Get Detail Consumer by Email .
   *
   * @param email GPoin Consumer's Email
   * @return Detail Consumer
   */
  public Consumer getProfileByEmail(String email) {

    Optional<Consumer> existConsumer = consumerRepository.findOneByEmail(email);

    return existConsumer.orElseGet(Consumer::new);

  }

  /**
   * Get Detail of Consumer by PS ID .
   *
   * @param psId GPoin Unique ID
   * @return Detail Consumer
   */
  public Consumer getProfileByPsId(String psId) {

    Consumer consumer = consumerRepository.findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));

    consumer.setHash(securityUtil.getHashForPsId(psId));
    return consumer;
  }

  private boolean isVerificationCodeNotExpired(Consumer consumer) {
    Date emailVerificationCodeCreatedTime = consumer.getEmailVerificationCodeCreatedTime();
    Date timeNow = DateUtil.getTimeNow();
    int hoursBetween = DateUtil.hoursBetween(timeNow, emailVerificationCodeCreatedTime);

    log.debug("isVerificationCodeNotExpired::hoursBetween : {}", hoursBetween);

    return hoursBetween < EMAIL_VERIFICATION_CODE_EXPIRY_IN_HOURS;
  }

  /**
   * Get Generated New Consumer by Social Id .
   *
   * @param entity Form Data Request Body
   * @param clientId Client Id (ex : womantalk)
   * @return New Detail Consumer
   */
  @Transactional
  public Consumer getGeneratedNewConsumerBySocialId(MultiValueMap<String, String> entity,
      String clientId) {
    String cleansedName = entity.getFirst("name").replaceAll("<[^>]*>", "");
    ConsumerParam consumerParam = new ConsumerParam.Builder().clientId(clientId)
        .name(cleansedName).socialType(entity.getFirst("social_media_type"))
        .socialId(entity.getFirst("social_media_id")).openingBalance(0)
        .email(entity.getFirst("email"))
        .emailVerified(Boolean.valueOf(entity.getFirst("email_verification_status"))).build();

    String psId = generateUniquePsId();
    return addNewConsumerBySocialId(consumerParam, psId);
  }

  @Transactional
  private Consumer addNewConsumerBySocialId(ConsumerParam consumerParam, String psId) {
    if (consumerParam.getSocialId() != null && consumerParam.getSocialId().length() != 0) {
      UUID uid = UUID.randomUUID();
      String hashCode = uid.toString();

      Client client = clientRepository.findOneByClientId(consumerParam.getClientId())
          .orElseThrow(() -> new ClientNotFoundException(consumerParam.getClientId()));

      Optional<Consumer> checkConsumer = getExistsConsumerBySocialId(consumerParam.getSocialId(),
          consumerParam.getSocialType());

      throwExceptionWhenEmailAlreadyExists(consumerParam.getEmail());

      if (!checkConsumer.isPresent()) {
        Consumer candidate = generateNewConsumerViaSocialMedia(consumerParam, psId, hashCode,
            client);

        ClientConsumer clientConsumer = buildClientConsumer(consumerParam.getClientId(),
            consumerParam.getOpeningBalance(), psId);
        clientConsumerRepository.saveAndFlush(clientConsumer);

        return candidate;
      }
      throw new ConsumerAlreadyExistException(RestStatus.CONSUMER_EXIST.getReasonPhrase());

    }
    throw new SocialMediaException(RestStatus.SOCIAL_ID_REQUIRED.getReasonPhrase());
  }

  private Optional<Consumer> getExistsConsumerBySocialId(String socialId, String socialType) {
    Optional<Consumer> checkConsumer;
    if (socialType.equalsIgnoreCase("facebook")) {
      checkConsumer = consumerRepository.findOneByFacebookId(socialId);
    } else if (socialType.equalsIgnoreCase("twitter")) {
      checkConsumer = consumerRepository.findOneByTwitterId(socialId);
    } else {
      throw new SocialMediaException("Social Media Type Undefined");
    }
    return checkConsumer;
  }

  private void throwExceptionWhenEmailAlreadyExists(String email) {
    Optional<Consumer> optConsumer = consumerRepository
        .findOneByEmail(email);
    if (optConsumer.isPresent()) {
      throw new EmailAlreadyExistException(email);
    }
  }

  private Consumer generateNewConsumerViaSocialMedia(ConsumerParam consumerParam, String psId,
      String hashCode, Client client) {
    TrialBalance trialBalance = buildNewTrialBalanceForNewConsumer(consumerParam
        .getOpeningBalance());
    Consumer candidate = buildNewConsumerViaSocialMedia(consumerParam, psId, hashCode, client,
        trialBalance);
    candidate = getGeneratedConsumerWhenNotExist(candidate);
    return candidate;
  }

  private Consumer buildNewConsumerViaSocialMedia(ConsumerParam consumerParam,
      String psId, String hashCode, Client client, TrialBalance trialBalance) {
    Consumer candidate = new Consumer();
    candidate.setPsId(psId);
    candidate.setEmail(consumerParam.getEmail());
    candidate.setRegisterFrom(client);
    candidate.setHashCode(hashCode);
    candidate.setTrialBalance(trialBalance);
    candidate.setStatus(1);
    candidate.setPassword(null);
    candidate.setTemporaryPassword(null);
    candidate.setName(consumerParam.getName());
    candidate.setEmailVerified(consumerParam.getEmailVerified());
    if (consumerParam.getSocialType().equalsIgnoreCase("facebook")) {
      candidate.setFacebookId(consumerParam.getSocialId());
    } else if (consumerParam.getSocialType().equalsIgnoreCase("twitter")) {
      candidate.setTwitterId(consumerParam.getSocialId());
    }
    return candidate;
  }

  private Consumer getGeneratedConsumerWhenNotExist(Consumer candidate) {
    try {
      candidate = consumerRepository.saveAndFlush(candidate);
    } catch (DataIntegrityViolationException div) {
      throw new ConsumerAlreadyExistException(RestStatus.CONSUMER_EXIST.getReasonPhrase());
    }
    return candidate;
  }

  private Consumer getGeneratedConsumerWhenNotExist(String email, Consumer candidate) {
    try {
      candidate = consumerRepository.saveAndFlush(candidate);
    } catch (DataIntegrityViolationException div) {
      throw new EmailAlreadyExistException(email);
    }
    return candidate;
  }

  /**
   * Get Generated New Consumer by Email .
   *
   * @param email GPoin Consumer's Email
   * @param clientId Client Id (ex: womantalk)
   * @param name GPoin Consumer's Name
   * @param openingBalance Opening Balance
   * @return New Detail Consumer
   */
  @Transactional
  public Consumer getGeneratedConsumerByEmail(String email, String clientId, String name,
      Integer openingBalance) {

    Optional<Consumer> consumer = consumerRepository.findOneByEmail(email);

    if (consumer.isPresent()) {
      return consumer.get();
    } else {
      String psId = generateUniquePsId();
      String cleansedName = name.replaceAll("<[^>]*>", "");
      return addNewConsumerByEmail(email, clientId, cleansedName, openingBalance, psId);
    }

  }

  @Transactional
  private Consumer addNewConsumerByEmail(String email, String clientId, String name,
      Integer openingBalance, String psId) {

    if (email != null) {
      UUID uid = UUID.randomUUID();
      String hashCode = uid.toString();

      Client client = setClientForNewConsumer(clientId);

      Optional<Consumer> checkConsumer = consumerRepository.findOneByEmail(email);

      if (!checkConsumer.isPresent()) {
        TrialBalance trialBalance = buildNewTrialBalanceForNewConsumer(openingBalance);

        Consumer candidate = buildNewConsumerViaEmail(email, name, psId, hashCode, client,
            trialBalance);

        candidate = getGeneratedConsumerWhenNotExist(email, candidate);

        ClientConsumer clientConsumer = buildClientConsumer(clientId, openingBalance, psId);

        clientConsumerRepository.saveAndFlush(clientConsumer);

        return candidate;
      }
      throw new EmailAlreadyExistException(email);

    }
    throw new EmailMustExistException();
  }

  private Client setClientForNewConsumer(String clientId) {
    Client client;
    if (clientId == null) {
      client = clientRepository.findOneByClientId(DEFAULT_CLIENT_ID)
          .orElseThrow(() -> new ClientNotFoundException(DEFAULT_CLIENT_ID));
    } else {
      client = clientRepository.findOneByClientId(clientId)
          .orElseThrow(() -> new ClientNotFoundException(clientId));
    }
    return client;
  }

  private TrialBalance buildNewTrialBalanceForNewConsumer(Integer openingBalance) {
    TrialBalance trialBalance = new TrialBalance();
    trialBalance.setOpeningBalance(openingBalance);
    trialBalance.setTotalDebits(0);
    trialBalance.setTotalCredits(0);
    trialBalance.setClosingBalance(openingBalance);
    return trialBalance;
  }

  private Consumer buildNewConsumerViaEmail(String email, String name, String psId, String hashCode,
      Client client, TrialBalance trialBalance) {
    Consumer candidate = new Consumer();
    candidate.setPsId(psId);
    candidate.setEmail(email);
    candidate.setRegisterFrom(client);
    candidate.setHashCode(hashCode);
    candidate.setTrialBalance(trialBalance);
    candidate.setStatus(1);
    candidate.setPassword(null);
    candidate.setName(name);
    candidate.setEmailVerified(false);
    candidate.setTemporaryPassword(null);
    return candidate;
  }

  private ClientConsumer buildClientConsumer(String clientId, Integer openingBalance, String psId) {
    ClientConsumer clientConsumer = new ClientConsumer();
    clientConsumer.setPsId(psId);
    clientConsumer.setRegisterFrom(clientId);
    clientConsumer.setClosingBalanceInPoinSystem(openingBalance);
    clientConsumer.setClosingBalanceInWomanTalk(openingBalance);
    clientConsumer.setIsMatch(true);
    return clientConsumer;
  }

  private String generateUniquePsId() {
    String psId = RandomStringUtils.randomNumeric(12);
    Optional<Consumer> checkPsId = consumerRepository.findOneByPsId(psId);

    if (checkPsId.isPresent()) {
      return generateUniquePsId();
    } else {
      return psId;
    }
  }

  /**
   * Get Detail Consumer After Sync Email Verification .
   *
   * @param clientId Client Id (ex : womantalk)
   * @param email GPoin Consumer's Email
   * @param psId GPoin Unique Id
   * @param hash Hashed PS ID
   * @return Detail Consumer
   */
  public Consumer getDetailAfterEmailVerificationHandling(String clientId, String email,
      String psId, String hash) {
    clientRepository.findOneByClientId(clientId)
        .orElseThrow(() -> new ClientNotFoundException(clientId));

    boolean matchHash = md5PasswordEncoder.isPasswordValid(hash, psId, Constant.SALT);

    if (matchHash) {
      Consumer consumerBeforeVerified =
          consumerRepository.findOneByPsId(psId)
              .orElseThrow(() -> new PsIdNotFoundException(psId));

      if (consumerBeforeVerified.getEmail().trim().toLowerCase()
          .equals(email.trim().toLowerCase())) {
        consumerBeforeVerified.setEmailVerified(true);

        return consumerRepository.saveAndFlush(consumerBeforeVerified);

      }
      throw new EmailDoesntMatchException(email);

    }
    throw new HashNotValidException(hash);

  }

  private void autoLogin(String username, String password) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(userDetails, password,
            userDetails.getAuthorities());

    authenticationManager.authenticate(usernamePasswordAuthenticationToken);

    if (usernamePasswordAuthenticationToken.isAuthenticated()) {
      SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      log.debug(String.format("Auto login %s successfully!", username));
    }
  }

  @Transactional
  void emailVerification(String emailVerificationCode) {
    Optional<Consumer> optConsumer = consumerRepository
        .findOneByEmailVerificationCode(emailVerificationCode);

    if (!optConsumer.isPresent()) {
      throw new ConsumerEmailVerificationException();
    }

    Consumer consumer = optConsumer.get();
    if (isVerificationCodeNotExpired(consumer)) {
      handleNotExpiredVerificationCode(consumer);
    } else {
      handleExpiredVerificationCode(consumer);
    }
  }

  private void handleNotExpiredVerificationCode(Consumer consumer) {
    consumer.setEmailVerificationCodeCreatedTime(null);
    consumer.setEmailVerificationCode(null);
    consumer.setEmailVerified(true);
    consumerRepository.saveAndFlush(consumer);
  }

  private void handleExpiredVerificationCode(Consumer consumer) {
    consumer.setEmailVerificationCodeCreatedTime(null);
    consumer.setEmailVerificationCode(null);
    consumerRepository.saveAndFlush(consumer);

    throw new ConsumerEmailVerificationException();
  }

  Integer getConsumerStatus(String psId, HttpServletRequest request, Principal principal) {
    Consumer consumer = consumerRepository.findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));

    return getConsumerStatusBasedOnCertainCondition(consumer, psId, request, principal);
  }

  private Integer getConsumerStatusBasedOnCertainCondition(Consumer consumer, String psId,
      HttpServletRequest request, Principal principal) {

    if (!consumer.getEmailVerified() || consumer.getEmail() == null) {
      return ConsumerLoginStatus.EMAIL_NOT_VERIFIED.getValue();
    }

    if (consumer.getPassword() != null && consumer.getTemporaryPassword() != null
        || consumer.getPassword() == null && consumer.getTemporaryPassword() != null) {
      return ConsumerLoginStatus.FORGOT_PIN.getValue();
    }

    if (consumer.getPassword() == null && consumer.getTemporaryPassword() == null) {
      return ConsumerLoginStatus.DOESNT_HAS_PIN.getValue();
    }

    try {
      UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
          .getAuthentication().getPrincipal();
      return getLoginOrLogoutStatusByCompareSessionAndPsId(psId, request, principal);
    } catch (ClassCastException e) {
      return ConsumerLoginStatus.LOGOUT.getValue();
    }
  }

  private Integer getLoginOrLogoutStatusByCompareSessionAndPsId(String psId,
      HttpServletRequest request, Principal principal) {
    if (!Objects.equals(principal.getName(), psId)) {
      SecurityContextHolder.clearContext();
      HttpSession session = request.getSession(false);
      invalidateSessionIfNotNull(session);
      return ConsumerLoginStatus.LOGOUT.getValue();
    } else {
      return ConsumerLoginStatus.ALREADY_LOGIN.getValue();
    }
  }

  private void invalidateSessionIfNotNull(HttpSession session) {
    if (session != null) {
      session.invalidate();
    }
    SecurityContextHolder.clearContext();
  }

  @Transactional
  void generateNewPasswordByPsId(String psId, int flagPinGeneratedStatus) {
    Consumer consumer = consumerRepository.findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));

    String emailSubject = getEmailSubjectByConsumerLoginStatus(flagPinGeneratedStatus);

    generateNewPin(consumer, emailSubject);
  }

  private String getEmailSubjectByConsumerLoginStatus(int loginStatus) {
    String emailSubject = null;
    if (loginStatus == ConsumerLoginStatus.FORGOT_PIN.getValue()) {
      emailSubject = gpoinEmailResetPinSubject;
    } else if (loginStatus == ConsumerLoginStatus.DOESNT_HAS_PIN.getValue()) {
      emailSubject = gpoinEmailGeneratePinSubject;
    }
    return emailSubject;
  }

  private void generateNewPin(Consumer consumer, String emailSubject) {
    String generatedPin = HashUtil.generateNewPassword();
    consumer.setTemporaryPassword(myPasswordEncoder.encode(generatedPin));

    consumerRepository.saveAndFlush(consumer);
    setConsumerActivityLog(consumer);

    String defaultName = "Member";

    String template = emailTemplateGenerator
        .generateEmailNewPin(generatedPin, consumer, defaultName);
    emailBlaster.send(consumer.getEmail(), emailSubject, template);

  }

  private void setConsumerActivityLog(Consumer consumer) {
    ConsumerActivityLog consumerActivityLog = new ConsumerActivityLog();
    consumerActivityLog.setConsumer(consumer);

    if (consumer.getPassword() != null) {
      consumerActivityLog.setActivity(ConsumerActivity.FORGOT_PIN.value());
    } else {
      consumerActivityLog.setActivity(ConsumerActivity.GENERATE_NEW_PIN.value());
    }
    consumerActivityRepository.save(consumerActivityLog);
  }

  Boolean isPasswordEqualsToDatabase(String psId, String password) {
    Consumer consumer = consumerRepository.findTemporaryPasswordByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));
    if (consumer.getTemporaryPassword() == null) {
      throw new PasswordException("You are already change your PIN !");
    }
    return myPasswordEncoder.matches(password, consumer.getTemporaryPassword());
  }

  void changePasswordAfterFirstVerification(String psId, String oldPassword, String newPassword) {
    if (isPasswordEqualsToDatabase(psId, oldPassword)) {
      Consumer consumer = consumerRepository.findOneByPsId(psId)
          .orElseThrow(() -> new PsIdNotFoundException(psId));

      if (newPassword.matches(PASSWORD_PATTERN)) {
        String hashNewPassword = myPasswordEncoder.encode(newPassword);
        consumer.setPassword(hashNewPassword);
        consumer.setTemporaryPassword(null);
        consumerRepository.saveAndFlush(consumer);
        autoLogin(psId, newPassword);
      } else {
        throw new PasswordException("Your password must six numeric");
      }
    } else {
      throw new PasswordException();
    }
  }

  /**
   * Change Password and Auto Logout .
   *
   * @param psId GPoin Unique Id
   * @param oldPassword GPoin Consumer's Old Password
   * @param newPassword GPoin Consumer's New Password
   * @param request Http Servlet Request (Get Existing Session)
   */
  void changePassword(String psId, String oldPassword, String newPassword,
      HttpServletRequest request) {

    Consumer consumer = consumerRepository.findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));

    if (myPasswordEncoder.matches(oldPassword, consumer.getPassword())) {
      if (newPassword.matches(PASSWORD_PATTERN)) {
        String hashedNewPassword = myPasswordEncoder.encode(newPassword);
        consumer.setPassword(hashedNewPassword);
        consumerRepository.saveAndFlush(consumer);

        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        invalidateSessionIfNotNull(session);
      } else {
        throw new NumericPasswordException();
      }
    } else {
      throw new PasswordException();
    }

  }

  /**
   * Get Updated Detail Consumer .
   *
   * @param psId GPoin Unique Id
   * @param name GPoin Consumer's Name
   * @param email GPoin Consumer's Email
   * @return Detail Consumer
   */
  public Consumer getUpdatedConsumer(String psId, String name, String email) {
    Consumer consumer = consumerRepository.findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));
    if (email != null) {
      if (consumer.getEmail() != null) {
        if (!consumer.getEmail().equals(email)) {
          consumer.setEmailVerified(false);
          consumer.setEmail(email);
        }
      } else {
        consumer.setEmail(email);
        consumer.setEmailVerified(false);
      }
    }
    consumer.setName(name.replaceAll("<[^>]*>", ""));

    consumerRepository.saveAndFlush(consumer);

    return consumer;
  }

  Consumer getUpdatedConsumer(String psId, MultiValueMap<String, String> entity) {

    Consumer consumer = consumerRepository.findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));

    consumer.setName(entity.getFirst("name").replaceAll("<[^>]*>", ""));
    consumer.setRealName(entity.getFirst("real_name").replaceAll("<[^>]*>", ""));
    consumer.setGender(entity.getFirst("gender").replaceAll("<[^>]*>", ""));
    consumer.setDateOfBirth(new Timestamp(Long.valueOf(entity.getFirst("date_of_birth"))));
    consumer.setPhoneNumber(entity.getFirst("phone_number").replaceAll("<[^>]*>", ""));
    consumer.setAddress(entity.getFirst("address").replaceAll("<[^>]*>", ""));
    consumer.setCity(entity.getFirst("city").replaceAll("<[^>]*>", ""));
    consumer.setPostCode(Integer.valueOf(entity.getFirst("postal_code").replaceAll("<[^>]*>", "")));
    consumer.setIdentityNumber(entity.getFirst("identity_number").replaceAll("<[^>]*>", ""));
    consumer.setIdentityImage(entity.getFirst("identity_image"));
    consumer.setTaxImage(entity.getFirst("tax_image"));
    consumer.setHobby(entity.getFirst("hobby").replaceAll("<[^>]*>", ""));

    if (consumer.getConsumerSocialMedia() == null) {
      consumer.setConsumerSocialMedia(buildNewConsumerSocialMedia(entity));
    } else {
      consumer.getConsumerSocialMedia().setFacebookAccount(entity.getFirst("facebook_account").replaceAll("<[^>]*>", ""));
      consumer.getConsumerSocialMedia().setTwitterAccount(entity.getFirst("twitter_account").replaceAll("<[^>]*>", ""));
      consumer.getConsumerSocialMedia().setInstagramAccount(entity.getFirst("instagram_account").replaceAll("<[^>]*>", ""));
    }
    return consumerRepository.saveAndFlush(consumer);
  }

  private ConsumerSocialMedia buildNewConsumerSocialMedia(MultiValueMap<String, String> entity)  {
    ConsumerSocialMedia consumerSocialMedia = new ConsumerSocialMedia();

    consumerSocialMedia.setFacebookAccount(entity.getFirst("facebook_account").replaceAll("<[^>]*>", ""));
    consumerSocialMedia.setTwitterAccount(entity.getFirst("twitter_account").replaceAll("<[^>]*>", ""));
    consumerSocialMedia.setInstagramAccount(entity.getFirst("instagram_account").replaceAll("<[^>]*>", ""));
    consumerSocialMediaRepository.saveAndFlush(consumerSocialMedia);

    return consumerSocialMedia;
  }

  /**
   * Get Detail of Consumer .
   *
   * @param psId GPoin Unique ID
   * @param password GPoin Member's Password
   * @return Consumer Detail
   */
  public Consumer getConsumerWhenPinAuthenticated(String psId, String password) {
    Consumer consumer = consumerRepository
        .findOneByPsId(psId)
        .orElseThrow(() -> new PsIdNotFoundException(psId));

    if (!myPasswordEncoder.matches(password, consumer.getPassword())) {
      throw new PasswordException("Your pin is not valid");
    }
    return consumer;
  }

  void emailBlastAdminWhenPsIdIsNullFromClient(String emailOrSocialId) {
    emailBlaster.sendMultipleRecipients("christian.e.saputra@gvmnetworks.com, "
            + "senoaji.wijaya@gvmnetworks.com, riky@gvmnetworks.com",
        "[PS ID NULL REPORT]", emailOrSocialId
            + " : Identitas ini tidak memiliki ps id");
  }
}