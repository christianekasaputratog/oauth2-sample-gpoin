package org.gvm.product.gvmpoin.module.rewardsystem.partner;

import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
@RestController
@RequestMapping(Constant.API_URL_V1 + RewardSystemConfig.MODULE_PATH + "/partner")
public class PartnerController {

  @Autowired
  private PartnerService partnerService;

  /**
   * Get All Partner for App.
   *
   * @return List of Partner Response Model
   */
  @JsonView(PsJsonView.RewardSystemPartner.class)
  @GetMapping("/all")
  public ResponseEntity<Response<List<Partner>>> getAllPartner() {

    List<Partner> partners = partnerService.getAllForClient();

    return PoinResponseEntityBuilder.buildFromThis(partners, HttpStatus.OK, HttpStatus.OK.value());
  }
}
