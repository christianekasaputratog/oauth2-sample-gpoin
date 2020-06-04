package org.gvm.product.gvmpoin.module.consumer;

import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.configuration.security.WebApiAuthenticationService;
import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.common.RestStatus;
import org.gvm.product.gvmpoin.module.common.S3.AmazonClient;
import org.gvm.product.gvmpoin.module.consumer.client.ConsumerClientService;
import org.gvm.product.gvmpoin.module.consumer.wishlist.ConsumerWishList;
import org.gvm.product.gvmpoin.module.consumer.wishlist.ConsumerWishListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(Constant.WEB_API_URL + "/consumer")
public class ConsumerWebApiController {

  private final Logger logger = LoggerFactory.getLogger(getClass());


  @Autowired
  ConsumerService consumerService;

  @Autowired
  UserDetailsService userDetailsService;

  @Autowired
  WebApiAuthenticationService webApiAuthenticationService;

  @Autowired
  ConsumerWishListService consumerWishListService;

  @Autowired
  ConsumerClientService consumerClientService;

  @Autowired
  AmazonClient amazonClient;

  /**
   * Get Detail of GPoin Member .
   *
   * @param psId GPoin Unique ID
   * @param principal User Authentication
   * @return Detail consumer or Null Return
   */
  @JsonView(PsJsonView.ConsumerWithBalance.class)
  @GetMapping("/get_profile")
  public ResponseEntity<Response<Consumer>> getLoggedProfile(
      @RequestParam("ps_id") String psId, Principal principal) {
    if (principal.getName().equals(psId)) {
      Consumer consumer = consumerService.getProfileByPsId(psId);
      return PoinResponseEntityBuilder.buildFromThis(consumer, HttpStatus.OK,
          HttpStatus.OK.value());
    } else {
      return PoinResponseEntityBuilder
          .buildFromThis(null, HttpStatus.OK, RestStatus.NOT_MATCH.value());
    }
  }

  /**
   * Get GPoin Member's Login Status .
   *
   * @param psId GPoin Unique ID
   * @param request HTTP Servlet Request
   * @param principal User Authentication
   * @return Login Status in Integer
   */
  @GetMapping("/status")
  public ResponseEntity<Response<Integer>> checkConsumerLoginStatus(
      @RequestParam("ps_id") String psId, HttpServletRequest request, Principal principal) {

    Integer consumerLoginStatus = consumerService.getConsumerStatus(psId, request, principal);

    return PoinResponseEntityBuilder.buildFromThis(consumerLoginStatus, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Generate new PIN for new GPoin Member .
   *
   * @param entity Form Data Request Body
   * @return String Success
   */
  @PostMapping("/get_pin")
  public ResponseEntity<Response<String>> generateNewPassword(
      @RequestBody MultiValueMap<String, String> entity) {
    consumerService.generateNewPasswordByPsId(entity.getFirst("ps_id"),
        ConsumerLoginStatus.DOESNT_HAS_PIN.getValue());
    return PoinResponseEntityBuilder.buildFromThis(Constant.API_STRING_SUCCESS, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Reset PIN and Email to GPoin Member .
   *
   * @param entity Form Data Request Body
   * @return String Success
   */
  @PostMapping("/forgot_pin")
  public ResponseEntity<Response<String>> forgotPassword(
      @RequestBody MultiValueMap<String, String> entity) {
    consumerService.generateNewPasswordByPsId(entity.getFirst("ps_id"),
        ConsumerLoginStatus.FORGOT_PIN.getValue());
    return PoinResponseEntityBuilder.buildFromThis(Constant.API_STRING_SUCCESS, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Reset PIN and Email to GPoin Member + Logout When Buy Reward.
   *
   * @param entity Form Data Request Body
   * @return String Success
   */
  @PostMapping("/buy/forgot_pin")
  public ResponseEntity<Response<String>> forgotPasswordAndLogout(HttpServletRequest request,
      @RequestBody MultiValueMap<String, String> entity) {
    consumerService.generateNewPasswordByPsId(entity.getFirst("ps_id"),
        ConsumerLoginStatus.FORGOT_PIN.getValue());

    webApiAuthenticationService.logout(request);

    return PoinResponseEntityBuilder.buildFromThis(Constant.API_STRING_SUCCESS, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Change old PIN to new PIN after email verification .
   *
   * @param entity Form Data Request Body
   * @return String Success
   */
  @PostMapping("/pin")
  public ResponseEntity<Response<String>> changePasswordAfterEmailVerification(
      @RequestBody MultiValueMap<String, String> entity) {
    consumerService
        .changePasswordAfterFirstVerification(entity.getFirst("ps_id"), entity.getFirst("old_pin"),
            entity.getFirst("new_pin"));
    return PoinResponseEntityBuilder.buildFromThis(Constant.API_STRING_SUCCESS, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Change old PIN to new PIN .
   *
   * @param entity Form Data Request Body
   * @return String Success
   */
  @PostMapping("/change/pin")
  public ResponseEntity<Response<String>> changePassword(
      @RequestBody MultiValueMap<String, String> entity, HttpServletRequest request) {
    consumerService
        .changePassword(entity.getFirst("ps_id"), entity.getFirst("old_pin"),
            entity.getFirst("new_pin"), request);
    return PoinResponseEntityBuilder.buildFromThis(Constant.API_STRING_SUCCESS, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Update Profile of GPoin Member .
   *
   * @param entity Form Data Request Body
   * @return Updated GPoin Member's Detail
   */
  @JsonView(PsJsonView.ConsumerWithBalance.class)
  @PostMapping("/profile")
  public ResponseEntity<Response<Consumer>> changeProfile(
      @RequestBody MultiValueMap<String, String> entity) {
    Consumer consumer = consumerService.getUpdatedConsumer(entity.getFirst("ps_id"), entity);

    return PoinResponseEntityBuilder.buildFromThis(consumer, HttpStatus.OK, HttpStatus.OK.value());
  }

  /**
   * Send Email to Developer and PO of GPoin about Null PS ID .
   *
   * @param entity Form Data Request Body
   * @return String Success
   */
  @PostMapping("/null_ps_id")
  public ResponseEntity<Response<String>> emailBlastWhenPsIdIsNullFromClient(
      @RequestBody MultiValueMap<String, String> entity) {
    consumerService.emailBlastAdminWhenPsIdIsNullFromClient(entity.getFirst("identity"));
    return PoinResponseEntityBuilder.buildFromThis(Constant.API_STRING_SUCCESS, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Add Consumer Wish List .
   *
   * @param entity Form Data Request Body
   * @return String Success
   */
  @JsonView(PsJsonView.Consumer.class)
  @PostMapping("/add/wishlist")
  public ResponseEntity<Response<String>> addNewConsumerWishList(
      @RequestBody MultiValueMap<String, String> entity) {
    logger.info("ADD NEW CONSUMER WISHLIST WEB CONTROLLER EXECUTED !");

    consumerWishListService.addNewConsumerWishList(entity);

    return PoinResponseEntityBuilder.buildFromThis(Constant.API_STRING_SUCCESS, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Remove Consumer Wish List .
   *
   * @param entity Form Data Request Body
   * @return String Success
   */
  @JsonView(PsJsonView.Consumer.class)
  @PostMapping("/remove/wishlist")
  public ResponseEntity<Response<String>> removeConsumerWishList(
      @RequestBody MultiValueMap<String, String> entity) {
    logger.info("REMOVE CONSUMER WISHLIST WEB CONTROLLER EXECUTED !");

    consumerWishListService.removeConsumerWishList(entity);

    return PoinResponseEntityBuilder.buildFromThis(Constant.API_STRING_SUCCESS, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Get List of Consumer Wishlist .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param psId GPoin Number
   * @return List of Consumer Wishlist Response Model
   */
  @JsonView(PsJsonView.Consumer.class)
  @GetMapping("/wishlist")
  public ResponseEntity<Response<List<ConsumerWishList>>> getListOfConsumerWishList(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
      @RequestParam("ps_id") String psId, @RequestParam("client_id") String clientId) {

    logger.info("GET CONSUMER WISHLIST WEB CONTROLLER EXECUTED !");

    List<ConsumerWishList> rewards = consumerWishListService
        .getListOfConsumerWishList(psId, clientId, page, size);

    return PoinResponseEntityBuilder.buildFromThis(rewards, HttpStatus.OK, HttpStatus.OK.value());
  }

  @PostMapping("/sync/client")
  public ResponseEntity<Response<String>> syncConsumerClientByRegisterFrom() {

    logger.info("SYNC CONSUMER CLIENT BY REGISTER WEB CONTROLLER EXECUTED !");

    consumerClientService.buildNewBySyncWithConsumer();

    return PoinResponseEntityBuilder.buildFromThis("success", HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Save updated profile image .
   *
   * @param imageFile updated image profile
   * @return String link S3
   */
  @PostMapping("/profile/image")
  public ResponseEntity<Response<String>> uploadImageProfileToS3(
      @RequestParam(value = "image_file") MultipartFile imageFile) throws IOException {

    return PoinResponseEntityBuilder.buildFromThis(amazonClient.uploadFile(imageFile), HttpStatus.OK,
        HttpStatus.OK.value());
  }
}