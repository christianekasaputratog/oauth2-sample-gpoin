package org.gvm.product.gvmpoin.module.consumer.integration.v1;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping(Constant.API_URL_V1 + "/consumer")
public class ConsumerIntegrationV1ApiController {

  @Autowired
  private ConsumerService consumerService;

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
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String clientId = ((OAuth2Authentication) authentication).getOAuth2Request().getClientId();

    Response<Consumer> response = new Response<>();
    Consumer consumer = consumerService
        .getGeneratedConsumerByEmail(entity.getFirst("email"), clientId,
            entity.getFirst("name"), 0);
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
   * @param email Email's GPoin Member
   * @param psId GPoin Unique ID
   * @param hash Hashed PS ID
   * @return Detail of Verified GPoin Member
   */
  @JsonView(PsJsonView.Consumer.class)
  @PostMapping("/notify_email_verified")
  public ResponseEntity<Response<Consumer>> notifyEmailVerified(@RequestParam("email") String email,
      @RequestParam("ps_id") String psId, @RequestParam("hash") String hash) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String clientId = ((OAuth2Authentication) authentication).getOAuth2Request().getClientId();

    Response<Consumer> response = new Response<>();
    Consumer consumer = consumerService
        .getDetailAfterEmailVerificationHandling(clientId, email, psId, hash);
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
    Consumer consumer = consumerService.getUpdatedConsumer(entity.getFirst("ps_id"),
        entity.getFirst("name"), entity.getFirst("email"));

    return PoinResponseEntityBuilder.buildFromThis(consumer, HttpStatus.OK, HttpStatus.OK.value());
  }

  /**
   * Get Detail Consumer by PS ID .
   *
   * @param psId GPoin Unique Id
   * @return Detail Consumer
   */
  @JsonView(PsJsonView.Consumer.class)
  @GetMapping("/profile")
  public ResponseEntity<Response<Consumer>> getDetailConsumerByPsId(
      @RequestParam("ps_id") String psId) {

    Response<Consumer> response = new Response<>();
    Consumer consumer = consumerService.getProfileByPsId(psId);
    response.setStatus(HttpStatus.OK.value());
    response.setData(consumer);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * Get Detail Consumer by Email .
   *
   * @param email Email's GPoin Member
   * @return Detail of GPoin Member
   */
  @JsonView(PsJsonView.Consumer.class)
  @GetMapping("/profile/email")
  public ResponseEntity<Response<Consumer>> getMemberProfileByEmail(
      @RequestParam("email") String email) {
    Response<Consumer> response = new Response<>();
    Consumer consumer = consumerService.getProfileByEmail(email);
    response.setStatus(HttpStatus.OK.value());
    response.setData(consumer);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  /**
   * Get Existed Response Detail Consumer by Email.
   *
   * @param email Email's GPoin Member
   * @return Existed Response
   */
  @JsonView(PsJsonView.Consumer.class)
  @GetMapping("/identities/email")
  public ResponseEntity<Response<String>> getDetailProfileByEmail(
      @RequestParam("email") String email) {
    Response<String> response = new Response<>();
    String message = consumerService.getExistedProfileResponseByEmail(email);
    response.setStatus(HttpStatus.OK.value());
    response.setData(message);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
