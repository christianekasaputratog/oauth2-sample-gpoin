package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa;

import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/integrator")
public class SepulsaIntegratorController {

  @Autowired
  private SepulsaRestService sepulsaRestService;

  /**
   * Get Callback from Sepulsa .
   *
   * @param cdr Sepulsa Callback Model
   * @return String Response Model
   */
  @PostMapping("/sepulsa/callback/cdr")
  public ResponseEntity<Response<String>> callbackCdr(@RequestBody SepulsaCdr cdr) {

    sepulsaRestService.saveCdr(cdr);

    return PoinResponseEntityBuilder.buildFromThis("success",
        HttpStatus.OK, HttpStatus.OK.value());
  }

  /**
   * Test Redeem Sepulsa .
   *
   * @param voucherCode Sepulsa Voucher code
   * @param customerPhoneNumber Customer Phone Number
   * @return SepulsaRedeemResponse Model
   */
  @PostMapping("/sepulsa/test/redeem")
  public ResponseEntity<Response<SepulsaRedeemResponse>> testRedeem(
      @RequestParam(value = "voucher_code") String voucherCode,
      @RequestParam(value = "customer_number") String customerPhoneNumber) {

    SepulsaRedeemResponse sepulsaRedeemResponse = sepulsaRestService
        .redeemVoucherToSepulsaApi(voucherCode, customerPhoneNumber);

    return PoinResponseEntityBuilder.buildFromThis(sepulsaRedeemResponse, HttpStatus.OK,
        HttpStatus.OK.value());
  }
}
