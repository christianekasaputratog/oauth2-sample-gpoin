package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemConfig;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTaken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.text.ParseException;

/**
 * Created by marcelina.panggabean on 11/14/2017.
 */
@RestController
@RequestMapping(Constant.API_URL_V1 + RewardSystemConfig.MODULE_PATH + "/tada")
public class TadaController {

  @Autowired
  TadaService tadaService;

  @Autowired
  TadaSynchronizationService tadaSynchronizationService;

  @JsonView(PsJsonView.RewardSystemRewardTaken.class)
  @PostMapping("/redeem")
  public ResponseEntity<Response<RewardTaken>> redeemReward(
      Principal principal,
      @RequestBody MultiValueMap<String, String> entity) throws ParseException {

    RewardTaken rewardTaken = tadaService
        .getTadaRedemptionDetail(Long.parseLong(entity.getFirst("reward_taken_id")),
            entity.getFirst("cashier_code"), principal.getName());

    return PoinResponseEntityBuilder.buildFromThis(rewardTaken, HttpStatus.OK,
        HttpStatus.OK.value());
  }


}
