package org.gvm.product.gvmpoin.module.rewardsystem.reward;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.gvm.product.gvmpoin.module.common.PoinResponseEntityBuilder;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.common.Response;
import org.gvm.product.gvmpoin.module.common.RestStatus;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemConfig;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardcategory.RewardCategory;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardcategory.RewardCategoryService;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTaken;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenService;
import org.gvm.product.gvmpoin.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(Constant.WEB_API_URL + RewardSystemConfig.MODULE_PATH + "/reward")
public class RewardWebApiController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private RewardService rewardService;

  @Autowired
  private SecurityUtil securityUtil;

  @Autowired
  private RewardCategoryService rewardCategoryService;

  @Autowired
  private RewardTakenService rewardTakenService;

  /**
   * Get Popular Rewards Exclude by Reward ID .
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

    logger.info("GET REWARD POPULAR WEB CONTROLLER EXECUTED !");

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

    logger.info("GET LATEST REWARD WEB CONTROLLER EXECUTED !");

    List<Reward> rewards = rewardService.getLatest(page, size);

    return PoinResponseEntityBuilder.buildFromThis(rewards, HttpStatus.OK, HttpStatus.OK.value());
  }

  /**
   * Get Latest Rewards exclude Popular Rewards .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param psId GPoin Number
   * @param clientId Client Id pof Partner
   * @param createdTime Created Time Reward
   * @return List of Reward Response Model
   */
  @JsonView(PsJsonView.RewardSystemReward.class)
  @GetMapping("/latest/exclude/popular")
  public ResponseEntity<Response<List<Reward>>> getLatestExcludePopular(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam(value = "ps_id") String psId,
      @RequestParam(value = "client_id") String clientId,
      @RequestParam("created_time") Long createdTime) {

    logger.info("GET LATEST REWARD EXCLUDE POPULAR WEB CONTROLLER EXECUTED !");

    List<Reward> rewards = rewardService.getLatestExcludePopular(page, size, psId, clientId,
        createdTime);

    return PoinResponseEntityBuilder.buildFromThis(rewards, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Get Rewards By Ids and Status .
   *
   * @param rewardIds List of Reward Id
   * @return List of Reward Response Model
   */
  @JsonView(PsJsonView.RewardSystemReward.class)
  @GetMapping("/ids")
  public ResponseEntity<Response<List<Reward>>> getRewardByIdsAndStatus(
      @RequestParam("reward_ids") List<Long> rewardIds,
      @RequestParam("client_id") String clientId) {

    logger.info("GET REWARDS BY IDS AND STATUS WEB CONTROLLER EXECUTED !");

    List<Reward> rewards = rewardService.getRewardByIdsAndStatus(rewardIds, clientId);

    return PoinResponseEntityBuilder.buildFromThis(rewards, HttpStatus.OK, HttpStatus.OK.value());
  }


  /**
   * Get Reward Categories and their Rewards with limit .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param rewardSize Limit for Get Rewards per Category
   * @return List of Reward Category Response Model
   */
  @JsonView(PsJsonView.RewardSystemRewardCategory.class)
  @GetMapping("/categories")
  public ResponseEntity<Response<List<RewardCategory>>> getCategories(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam(value = "reward_size", required = false, defaultValue = "5")
          Integer rewardSize) {

    logger.info("GET REWARD CATEGORIES WEB CONTROLLER EXECUTED !");

    List<RewardCategory> categories = rewardCategoryService.getCategories(page, size, rewardSize);

    return PoinResponseEntityBuilder.buildFromThis(categories, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Get Reward Detail by ID .
   *
   * @param rewardId Reward ID
   * @return Reward Response Model
   */
  @JsonView(PsJsonView.RewardSystemReward.class)
  @GetMapping("")
  public ResponseEntity<Response<Reward>> getDetailReward(
      @RequestParam("reward_id") Long rewardId) {

    logger.info("GET DETAIL REWARD WEB CONTROLLER EXECUTED !");

    Reward reward = rewardService.getDetailRewardById(rewardId);

    return PoinResponseEntityBuilder.buildFromThis(reward, HttpStatus.OK,
        HttpStatus.OK.value());
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

    logger.info("GET REWARDS BY MERCHANT WEB CONTROLLER EXECUTED !");

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

    logger.info("GET REWARDS BY CATEGORY WEB CONTROLLER EXECUTED !");

    List<Reward> rewards = rewardCategoryService.getRewardsByCategoryId(page, size, categoryId);

    return PoinResponseEntityBuilder.buildFromThis(rewards, HttpStatus.OK, HttpStatus.OK.value());
  }

  /**
   * Get List of Active Reward Taken . (Status : Taken) .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param psId GPoin Unique ID
   * @param principal User Authentication
   * @return List of RewardTaken Response Model
   */
  @JsonView(PsJsonView.RewardSystemRewardTaken.class)
  @GetMapping("/taken/active")
  public ResponseEntity<Response<List<RewardTaken>>> getListOfActiveRewardVoucher(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam("ps_id") String psId, Principal principal) {

    logger.info("GET ACTIVE REWARDS WEB CONTROLLER EXECUTED !");

    if (psId.equals(principal.getName())) {
      List<RewardTaken> rewardTakens = rewardTakenService
          .getListOfActiveRewardTaken(psId, page, size);

      return PoinResponseEntityBuilder.buildFromThis(rewardTakens, HttpStatus.OK,
          HttpStatus.OK.value());
    } else {
      return PoinResponseEntityBuilder.buildFromThis(null, HttpStatus.OK,
          RestStatus.NOT_MATCH.value());
    }
  }

  /**
   * Get List of Used Reward Taken (Status : Redeemed and Expired) .
   *
   * @param page Equals to OFFSET Query
   * @param size Equals to LIMIT Query
   * @param psId GPoin Unique ID
   * @param principal User Authentication
   * @return List of RewardTaken Response Model
   */
  @JsonView(PsJsonView.RewardSystemRedeemedVoucher.class)
  @GetMapping("/taken/used")
  public ResponseEntity<Response<List<RewardTaken>>> getListOfUsedRewardVoucher(
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
      @RequestParam("ps_id") String psId, Principal principal) {

    logger.info("GET USED REWARDS WEB CONTROLLER EXECUTED !");

    if (psId.equals(principal.getName())) {
      List<RewardTaken> rewardTakens = rewardTakenService
          .getListOfInactiveRewardTaken(psId, page, size);

      return PoinResponseEntityBuilder.buildFromThis(rewardTakens, HttpStatus.OK,
          HttpStatus.OK.value());
    } else {
      return PoinResponseEntityBuilder.buildFromThis(null, HttpStatus.OK,
          RestStatus.NOT_MATCH.value());
    }
  }

  /**
   * Get Detail of Reward Taken .
   *
   * @param rewardTakenId Reward Taken ID
   * @param principal User Authentication
   * @return Reward Taken Response Model
   */
  @JsonView(PsJsonView.RewardSystemRewardTaken.class)
  @GetMapping("/taken")
  public ResponseEntity<Response<RewardTaken>> getDetailOfRewardTaken(
      @RequestParam(value = "reward_taken_id") Long rewardTakenId,
      Principal principal) {

    logger.info("GET DETAIL REWARD TAKEN WEB CONTROLLER EXECUTED !");

    RewardTaken rewardTaken = rewardTakenService
        .getDetailRewardTakenById(rewardTakenId, principal.getName(),
            securityUtil.getHashForPsId(principal.getName()));
    return PoinResponseEntityBuilder.buildFromThis(rewardTaken, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Take Reward (EGIFT, PULSA, EVENT, MERCHANDISE) .
   *
   * @param entity Form Data Request Body
   * @return Detail of Reward Taken
   * @throws ParseException Handle Error when buy egift from Tada
   */
  @JsonView(PsJsonView.RewardSystemRewardTaken.class)
  @PostMapping("/take")
  public ResponseEntity<Response<RewardTaken>> takeReward(
      @RequestBody MultiValueMap<String, String> entity) throws ParseException {

    logger.info("TAKE REWARD WEB CONTROLLER EXECUTED !");

    RewardTaken rewardTaken = rewardService.takeReward(entity);

    return PoinResponseEntityBuilder.buildFromThis(rewardTaken, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Redeem Reward (EGIFT, PULSA, EVENT, MERCHANDISE) .
   *
   * @param entity Form Data Request Body
   * @return Detail of Reward Taken
   */
  @JsonView(PsJsonView.RewardSystemRedeemedVoucher.class)
  @PostMapping("/redeem")
  public ResponseEntity<Response<RewardTaken>> redeemReward(
      @RequestBody MultiValueMap<String, String> entity) {

    logger.info("REDEEM REWARDS WEB CONTROLLER EXECUTED !");

    RewardTaken rewardTaken = rewardService.getRewardRedemptionDetail(entity);

    return PoinResponseEntityBuilder.buildFromThis(rewardTaken, HttpStatus.OK,
        HttpStatus.OK.value());
  }

  /**
   * Get Total of Active Reward Taken by PSID.
   *
   * @param psId GPoin Unique ID
   * @return Integer Total of Active Reward Taken Response Model
   */
  @JsonView(PsJsonView.RewardSystemRewardTaken.class)
  @GetMapping("/taken/active/total")
  public ResponseEntity<Response<Integer>> getTotalOfActiveRewardTaken(
      @RequestParam(value = "ps_id") String psId) {

    logger.info("GET TOTAL OF ACTIVE REWARD TAKEN CONTROLLER EXECUTED !");

    Integer totalOfActiveRewardTaken = rewardTakenService.getTotalOfActiveRewardTaken(psId);

    return PoinResponseEntityBuilder.buildFromThis(totalOfActiveRewardTaken, HttpStatus.OK,
        HttpStatus.OK.value());
  }
}