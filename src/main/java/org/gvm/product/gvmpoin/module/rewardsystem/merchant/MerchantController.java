package org.gvm.product.gvmpoin.module.rewardsystem.merchant;

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
@RequestMapping(Constant.API_URL_V1 + RewardSystemConfig.MODULE_PATH + "/merchant")
public class MerchantController {

  @Autowired
  private MerchantService merchantService;

  /**
   * Get Popular Merchant .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @return List of Merchant Response Model
   */
  @JsonView(PsJsonView.RewardSystemMerchant.class)
  @GetMapping("/popular")
  public ResponseEntity<Response<List<Merchant>>> getPopular(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size) {
    List<Merchant> merchants = merchantService.getPopular(page, size);

    return PoinResponseEntityBuilder.buildFromThis(merchants, HttpStatus.OK, HttpStatus.OK.value());
  }

  /**
   * Get All Merchants .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @return List of Merchant Response Model
   */
  @JsonView(PsJsonView.RewardSystemMerchant.class)
  @GetMapping("/all")
  public ResponseEntity<Response<List<Merchant>>> getAll(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size) {

    List<Merchant> merchants = merchantService.getAll(page, size);

    return PoinResponseEntityBuilder.buildFromThis(merchants, HttpStatus.OK, HttpStatus.OK.value());
  }
}
