package org.gvm.product.gvmpoin.module.consumer;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.API_URL_V1 + "/consumer/registration")
public class ConsumerRegistrationApiController {

  /**
   * Get Strength of GPoin Member's Password .
   * @param password GPoin Member's Password
   * @return Strength Percentage of Password
   */
  @GetMapping("/password-strength")
  public ResponseEntity<Response<Integer>> getPasswordStrength(
      @RequestParam("password") String password) {

    Integer strengthPercentage = ConsumerPasswordStrengthChecker.checkPercentage(password);

    return PoinResponseEntityBuilder.buildFromThis(strengthPercentage, HttpStatus.OK,
        HttpStatus.OK.value());
  }
}
