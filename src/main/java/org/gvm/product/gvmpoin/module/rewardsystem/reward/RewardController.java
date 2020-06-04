package org.gvm.product.gvmpoin.module.rewardsystem.reward;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemConfig;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardcategory.RewardCategory;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardcategory.RewardCategoryService;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTaken;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */

@RestController
@RequestMapping(Constant.API_URL_V1 + RewardSystemConfig.MODULE_PATH + "/reward")
public class RewardController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private RewardService rewardService;

  @Autowired
  private RewardCategoryService rewardCategoryService;

  @Autowired
  private RewardTakenService rewardTakenService;

  /**
   * Get Popular with Exclude Reward ID .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param psId Consumer GPoin Number
   * @param clientId Client Id of Partner
   * @return List of Reward Response Model
   */
  @JsonView(PsJsonView.RewardSystemReward.class)
  @GetMapping("/popular")
  public ResponseEntity<Response<List<Reward>>> getPopular(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam(value = "ps_id") String psId, @RequestParam("client_id") String clientId,
      @RequestParam("created_time") Long createdTime) {

    logger.info("GET REWARD POPULAR APP V1 CONTROLLER EXECUTED !");

    List<Reward> rewards = rewardService.getPopular(page, size, psId, clientId, createdTime);

    return PoinResponseEntityBuilder.buildFromThis(rewards, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Get Latest Rewards .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @return List of Reward Response Model
   */
  @JsonView(PsJsonView.RewardSystemReward.class)
  @GetMapping("/latest")
  public ResponseEntity<Response<List<Reward>>> getLatest(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size) {

    logger.info("GET LATEST REWARD APP V1 CONTROLLER EXECUTED !");

    List<Reward> rewards = rewardService.getLatest(page, size);

    return PoinResponseEntityBuilder.buildFromThis(rewards, HttpStatus.OK, HttpStatus.OK.value());
  }

  /**
   * Get Reward Categories .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param rewardSize Limit Size Request of Rewards per Category
   * @return List of Reward Category Response Model
   */
  @JsonView(PsJsonView.RewardSystemRewardCategory.class)
  @GetMapping("/categories")
  public ResponseEntity<Response<List<RewardCategory>>> getCategories(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam(value = "reward_size", required = false, defaultValue = "5")
          Integer rewardSize) {

    logger.info("GET REWARD CATEGORIES APP V1 CONTROLLER EXECUTED !");

    List<RewardCategory> categories = rewardCategoryService.getCategories(page, size, rewardSize);

    return PoinResponseEntityBuilder.buildFromThis(categories, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Get Detail of Reward .
   *
   * @param rewardId Reward ID
   * @return Reward Response Model
   */
  @JsonView(PsJsonView.RewardSystemReward.class)
  @GetMapping("")
  public ResponseEntity<Response<Reward>> getDetailReward(
      @RequestParam("reward_id") Long rewardId) {

    logger.info("GET DETAIL REWARD APP V1 CONTROLLER EXECUTED !");

    Reward reward = rewardService.getDetailRewardById(rewardId);

    return PoinResponseEntityBuilder.buildFromThis(reward, HttpStatus.OK, HttpStatus.OK.value());
  }

  /**
   * Get List of Reward by Merchant ID .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param merchantId Merchant ID
   * @return List of Reward Response Model
   */
  @JsonView(PsJsonView.RewardSystemReward.class)
  @GetMapping("/merchant")
  public ResponseEntity<Response<List<Reward>>> getListOfRewardByMerchantId(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam("merchant_id") Long merchantId) {

    logger.info("GET REWARDS BY MERCHANT APP V1 CONTROLLER EXECUTED !");

    List<Reward> rewards = rewardService.getByMerchantId(page, size, merchantId);

    return PoinResponseEntityBuilder.buildFromThis(rewards, HttpStatus.OK, HttpStatus.OK.value());
  }

  /**
   * Get List of Reward by Category ID .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param categoryId Category ID
   * @return List of Reward Response Model
   */
  @JsonView(PsJsonView.RewardSystemReward.class)
  @GetMapping("/category")
  public ResponseEntity<Response<List<Reward>>> getListOfRewardByCategoryId(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam("category_id") Long categoryId) {

    logger.info("GET REWARDS BY CATEGORY APP V1 CONTROLLER EXECUTED !");

    List<Reward> rewards = rewardCategoryService.getRewardsByCategoryId(page, size, categoryId);

    return PoinResponseEntityBuilder.buildFromThis(rewards, HttpStatus.OK, HttpStatus.OK.value());
  }

  /**
   * Get List of Active Reward by PS ID .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param psId GPoin Unique ID
   * @return List of Reward Taken Response Model
   */
  @JsonView(PsJsonView.RewardSystemRewardTaken.class)
  @GetMapping("/taken/active")
  public ResponseEntity<Response<List<RewardTaken>>> getListOfActiveRewardVoucher(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam(value = "ps_id") String psId) {

    logger.info("GET ACTIVE REWARDS APP V1 CONTROLLER EXECUTED !");

    List<RewardTaken> rewardTakens = rewardTakenService
        .getListOfActiveRewardTaken(psId, page, size);

    return PoinResponseEntityBuilder.buildFromThis(rewardTakens, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Get List of Used Reward by PS ID .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param psId GPoin Unique ID
   * @return List of Reward Taken Response Model
   */
  @JsonView(PsJsonView.RewardSystemRewardTaken.class)
  @GetMapping("/taken/used")
  public ResponseEntity<Response<List<RewardTaken>>> getListOfUsedRewardVoucher(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam(value = "ps_id") String psId) {

    logger.info("GET USED REWARDS APP V1 CONTROLLER EXECUTED !");

    List<RewardTaken> rewardTakens = rewardTakenService
        .getListOfInactiveRewardTaken(psId, page, size);

    return PoinResponseEntityBuilder.buildFromThis(rewardTakens, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Get Detail of Reward Taken by ID .
   *
   * @param rewardTakenId Reward Taken ID
   * @param psId GPoin Unique ID
   * @param hash Hashed PS ID
   * @return Reward Taken Response Model
   */
  @JsonView(PsJsonView.RewardSystemRewardTaken.class)
  @GetMapping("/taken")
  public ResponseEntity<Response<RewardTaken>> getDetailOfRewardTaken(
      @RequestParam(value = "reward_taken_id") Long rewardTakenId,
      @RequestParam(value = "ps_id") String psId, @RequestParam(value = "hash") String hash) {

    logger.info("GET DETAIL REWARD TAKEN APP V1 CONTROLLER EXECUTED !");

    RewardTaken rewardTaken = rewardTakenService
        .getDetailRewardTakenById(rewardTakenId, psId, hash);

    return PoinResponseEntityBuilder.buildFromThis(rewardTaken, HttpStatus.OK,
        HttpStatus.OK.value());
  }
}