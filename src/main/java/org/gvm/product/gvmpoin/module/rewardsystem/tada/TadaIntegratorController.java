package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/integrator")
public class TadaIntegratorController {

  @Autowired
  private TadaRestService tadaRestService;

  @Autowired
  TadaSynchronizationService tadaSynchronizationService;

  @Autowired
  private TadaService tadaService;

  /**
   * Test get access token from TADA .
   *
   * @return TadaAccessToken Response Model
   */
  @GetMapping("/tada/test/token")
  public ResponseEntity<Response<TadaAccessToken>> testToken() {
    TadaAccessToken accessToken = tadaRestService.getAccessToken();

    return PoinResponseEntityBuilder.buildFromThis(accessToken, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Synchronize E-Gift from TADA .
   * @return String
   */
  @PostMapping("/sync")
  public ResponseEntity<Response<String>> synchorization() {
    tadaSynchronizationService.synchronizeEGift();

    return PoinResponseEntityBuilder.buildFromThis("success", HttpStatus.OK,
        HttpStatus.OK.value());
  }

}
