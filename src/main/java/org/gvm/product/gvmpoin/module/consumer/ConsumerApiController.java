package org.gvm.product.gvmpoin.module.consumer;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(Constant.API_URL_V1 + "/consumer")
public class ConsumerApiController {

  @Autowired
  private ConsumerService consumerService;

  @Autowired
  private SyncConsumerFromCsvService syncConsumerFromCsvService;

  /**
   * Generate New PIN .
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
   * Forgot PIN .
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
   * Change PIN .
   *
   * @param entity Form Data Request Body
   * @return String Success
   */
  @PostMapping("/pin")
  public ResponseEntity<Response<String>> changePin(
      @RequestBody MultiValueMap<String, String> entity) {
    consumerService
        .changePasswordAfterFirstVerification(entity.getFirst("ps_id"), entity.getFirst("old_pin"),
            entity.getFirst("new_pin"));
    return PoinResponseEntityBuilder.buildFromThis(Constant.API_STRING_SUCCESS, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Integrate PS ID by Social Media from CSV File .
   *
   * @param file CSV File
   * @return String Success
   * @throws IOException Jika Akses File Error
   */
  @PostMapping("/csv/integrate/socialmedia")
  public ResponseEntity<Response<String>> generatePsIdBySocialMediaAccountCsv(
      @RequestParam("uploaded_file") MultipartFile file) throws IOException {
    syncConsumerFromCsvService.womantalkGeneratePsIdBySocialMediaCsvFile(file);
    return PoinResponseEntityBuilder.buildFromThis("success", HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Integrate PS ID by Email from CSV File .
   *
   * @param file CSV File
   * @return String Success
   * @throws IOException Jika Akses File Error
   */
  @PostMapping("/csv/integrate/email")
  public ResponseEntity<Response<String>> generatePsIdByEmailCsv(
      @RequestParam("uploaded_file") MultipartFile file) throws IOException {
    syncConsumerFromCsvService.womantalkGeneratePsIdByEmail(file);
    return PoinResponseEntityBuilder.buildFromThis("success", HttpStatus.OK,
        HttpStatus.OK.value());
  }

}
