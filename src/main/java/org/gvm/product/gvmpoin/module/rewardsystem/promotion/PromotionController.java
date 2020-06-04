package org.gvm.product.gvmpoin.module.rewardsystem.promotion;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
@RestController
@RequestMapping(Constant.API_URL_V1 + RewardSystemConfig.MODULE_PATH + "/promotion")
public class PromotionController {

  @Autowired
  private PromotionService promotionService;

  /**
   * Get Active Promotions For Apps.
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param clientId Equals to Client Id of Partner
   * @return List of Promotion Response Model
   */
  @JsonView(PsJsonView.RewardSystemPromotion.class)
  @GetMapping("/active")
  public ResponseEntity<Response<List<Promotion>>> getActivePromotions(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam("client_id") String clientId) {

    List<Promotion> promotions = promotionService.getActivePromotions(page, size, clientId);

    return PoinResponseEntityBuilder.buildFromThis(promotions, HttpStatus.OK,
        HttpStatus.OK.value());
  }
}