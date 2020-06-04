package org.gvm.product.gvmpoin.module.consumer.integration.v2;

import com.fasterxml.jackson.annotation.JsonView;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerService;
import org.gvm.product.gvmpoin.util.EncryptionUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.API_URL_V2 + "/consumer")
public class ConsumerIntegrationV2ApiController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private ConsumerService consumerService;

  /**
   * Get Detail of GPoin Member by Email .
   *
   * @param entity Encrypted Consumer's Email
   * @return Detail of GPoin Member
   */
  @JsonView(PsJsonView.Consumer.class)
  @GetMapping(value = "/get_psid_by_email", produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Response<String>> getPsIdByEmail(
      @RequestBody MultiValueMap<String, String> entity) {

    logger.info("GET PS_ID BY EMAIL API CONTROLLER V2 EXECUTED !");

    JSONObject object = EncryptionUtil.getObjectDecodedData(entity);
    String email = object.getString("email");

    Response<String> response = new Response<>();
    Consumer consumer = consumerService.getProfileByEmail(email);
    response.setStatus(HttpStatus.OK.value());
    response.setData(consumer.getPsId());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * Get Detail of GPoin Member by PS ID .
   *
   * @param entity Encrypted GPoin Unique ID
   * @return Detail of GPoin Member
   */
  @JsonView(PsJsonView.Consumer.class)
  @GetMapping("/get_profile_by_psid")
  public ResponseEntity<Response<Consumer>> getMemberProfileByPsId(
      @RequestBody MultiValueMap<String, String> entity) {

    logger.info("GET PROFILE BY PS_ID API CONTROLLER V2 EXECUTED !");

    JSONObject object = EncryptionUtil.getObjectDecodedData(entity);
    String psId = object.getString("ps_id");

    Response<Consumer> response = new Response<>();
    Consumer consumer = consumerService.getProfileByPsId(psId);
    response.setStatus(HttpStatus.OK.value());
    response.setData(consumer);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * Add New GPoin Member by Email .
   *
   * @param entity Form Data Request Body
   * @return Detail of New GPoin Member
   */
  @JsonView(PsJsonView.Consumer.class)
  @PostMapping("/add_by_email")
  public ResponseEntity<Response<Consumer>> addPsIdByEmail(
      @RequestBody MultiValueMap<String, String> entity) {

    logger.info("ADD NEW GPOIN MEMBER BY EMAIL API CONTROLLER V2 EXECUTED !");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String clientId = ((OAuth2Authentication) authentication).getOAuth2Request().getClientId();

    JSONObject object = EncryptionUtil.getObjectDecodedData(entity);

    Response<Consumer> response = new Response<>();
    Consumer consumer = consumerService
        .getGeneratedConsumerByEmail(object.getString("email"), clientId,
            object.getString("name"), 0);
    response.setStatus(HttpStatus.OK.value());
    response.setData(consumer);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * Add New GPoin Member by Social Media Account .
   *
   * @param entity Form Data Request Body
   * @return Detail of New GPoin Member
   */
  @JsonView(PsJsonView.Consumer.class)
  @PostMapping("/add_by_socialmedia")
  public ResponseEntity<Response<Consumer>> generatePsIdBySocialMediaId(
      @RequestBody MultiValueMap<String, String> entity) {

    logger.info("ADD NEW GPOIN MEMBER BY SOCIAL MEDIA API CONTROLLER V2 EXECUTED !");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String clientId = ((OAuth2Authentication) authentication).getOAuth2Request().getClientId();

    Response<Consumer> response = new Response<>();
    Consumer consumer = consumerService.getGeneratedNewConsumerBySocialId(entity, clientId);
    response.setStatus(HttpStatus.OK.value());
    response.setData(consumer);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * Verify Email by PS ID .
   *
   * @param entity Encrypted Email's GPoin Member, PS ID and Hashed PS ID
   * @return Detail of Verified GPoin Member
   */
  @JsonView(PsJsonView.Consumer.class)
  @PostMapping("/notify_email_verified")
  public ResponseEntity<Response<Consumer>> notifyEmailVerified(
      @RequestBody MultiValueMap<String, String> entity) {

    logger.info("NOTIFY EMAIL VERIFIED API CONTROLLER V2 EXECUTED !");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String clientId = ((OAuth2Authentication) authentication).getOAuth2Request().getClientId();

    JSONObject object = EncryptionUtil.getObjectDecodedData(entity);

    Response<Consumer> response = new Response<>();
    Consumer consumer = consumerService
        .getDetailAfterEmailVerificationHandling(clientId, object.getString("email"),
            object.getString("ps_id"), object.getString("hash"));
    response.setStatus(HttpStatus.OK.value());
    response.setData(consumer);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * Change Profile Detail .
   *
   * @param entity Form Data Request Body
   * @return Detail of Modified GPoin Member
   */
  @JsonView(PsJsonView.Consumer.class)
  @PostMapping("/profile")
  public ResponseEntity<Response<Consumer>> changeProfile(
      @RequestBody MultiValueMap<String, String> entity) {

    logger.info("GET UPDATED PROFILE GPOIN MEMBER API CONTROLLER V2 EXECUTED !");

    JSONObject object = EncryptionUtil.getObjectDecodedData(entity);

    Consumer consumer = consumerService.getUpdatedConsumer(object.getString("ps_id"),
        object.getString("name"), object.getString("email"));

    return PoinResponseEntityBuilder.buildFromThis(consumer, HttpStatus.OK, HttpStatus.OK.value());
  }

}
