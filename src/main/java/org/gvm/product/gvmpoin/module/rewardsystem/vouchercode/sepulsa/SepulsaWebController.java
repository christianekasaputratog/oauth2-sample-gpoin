package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa;

import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.common.RestStatus;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemConfig;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTaken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by marcelina.panggabean on 11/14/2017.
 */
@RestController
@RequestMapping(Constant.WEB_API_URL + RewardSystemConfig.MODULE_PATH + "/sepulsa")
public class SepulsaWebController {

  @Autowired
  SepulsaService sepulsaService;

  /**
   * [WEB] Redeem Sepulsa Voucher .
   *
   * @param entity Form Data Request Body
   * @return Reward Taken Response Model
   */
  @JsonView(PsJsonView.RewardSystemRewardTaken.class)
  @PostMapping("/redeem")
  public ResponseEntity<Response<RewardTaken>> redeemSepulsa(
      @RequestBody MultiValueMap<String, String> entity, Principal principal,
      @RequestParam(value = "ps_id") String psId) {
    if (psId.equals(principal.getName())) {
      RewardTaken rewardTaken = sepulsaService
          .getSepulsaRedemptionDetail(Long.parseLong(entity.getFirst("reward_taken_id")),
              entity.getFirst("customer_number"), principal.getName());

      return PoinResponseEntityBuilder
          .buildFromThis(rewardTaken, HttpStatus.OK, HttpStatus.OK.value());
    } else {
      return PoinResponseEntityBuilder
          .buildFromThis(null, HttpStatus.OK, RestStatus.NOT_MATCH.value());
    }
  }
}
