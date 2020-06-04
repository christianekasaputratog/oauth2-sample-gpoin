package org.gvm.product.gvmpoin.module.campaign.campaignreward;

import org.gvm.product.gvmpoin.module.campaign.CampaignService;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.LeaderboardParam;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.consumer.ConsumerService;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemException;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardGlobalFunction;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardStatus;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardType;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTaken;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenService;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenStatus;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceParam;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceService;
import org.gvm.product.gvmpoin.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Objects;

@Service
public class CampaignRewardService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private CampaignRewardRepository campaignRewardRepository;
  private RewardRepository rewardRepository;
  private RewardTakenRepository rewardTakenRepository;
  private RewardGlobalFunction rewardGlobalFunction;
  private CampaignService campaignService;
  private ConsumerService consumerService;
  private RewardTakenService rewardTakenService;
  private TrialBalanceService trialBalanceService;

  /**
   * Bean Configuration for Campaign Reward Service .
   *
   * @param campaignRewardRepository Campaign Reward Interface
   * @param rewardRepository Reward Interface
   * @param rewardTakenRepository Reward Taken Interface
   * @param rewardGlobalFunction Reward Global Function
   * @param campaignService Campaign Implementation
   * @param consumerService Consumer Implementation
   * @param rewardTakenService Reward Taken Implementation
   * @param trialBalanceService Trial Balance Implementation
   */

  @Autowired
  public CampaignRewardService(CampaignRewardRepository campaignRewardRepository,
      RewardRepository rewardRepository, RewardTakenRepository rewardTakenRepository,
      RewardGlobalFunction rewardGlobalFunction, CampaignService campaignService,
      ConsumerService consumerService, RewardTakenService rewardTakenService,
      TrialBalanceService trialBalanceService) {
    this.campaignRewardRepository = campaignRewardRepository;
    this.rewardRepository = rewardRepository;
    this.rewardTakenRepository = rewardTakenRepository;
    this.rewardGlobalFunction = rewardGlobalFunction;
    this.campaignService = campaignService;
    this.consumerService = consumerService;
    this.rewardTakenService = rewardTakenService;
    this.trialBalanceService = trialBalanceService;
  }

  /**
   * Take Reward for Point Reward Type .
   *
   * @param entity Form Data Request Body
   * @param consumer Detail of Consumer
   * @return Reward Taken Detail
   */
  @Transactional
  public RewardTaken getRewardTakenOfPointReward(MultiValueMap<String, String> entity,
      Consumer consumer) {

    logger.info("TAKE POINT REWARD SERVICE EXECUTED !");

    Reward reward = rewardRepository
        .findOneByIdForUpdate(Long.valueOf(entity.getFirst("reward_id")))
        .orElseThrow(() -> new RewardSystemException("Reward not found or already taken."));

    rewardGlobalFunction.throwExceptionWhenRewardStockIsEmpty(reward);
    rewardGlobalFunction.throwExceptionWhenRewardExpired(reward);

    CampaignReward campaignReward = campaignRewardRepository
        .findOneByRewardId(reward.getId());

    final LeaderboardParam subtractLeaderboardParam = rewardGlobalFunction
        .buildLeaderboardParamForTakeReward(consumer,
            campaignReward.getCampaignId().getCampaignUniqueCode(), reward.getPointCost(), reward);

    campaignService.getLeaderboarWithReducedValue(subtractLeaderboardParam);

    reward.setRemainStock(reward.getRemainStock() - 1);

    rewardRepository.saveAndFlush(reward);

    return getGeneratedRewardTakenOfPointReward(consumer, reward);
  }

  private RewardTaken getGeneratedRewardTakenOfPointReward(Consumer consumer, Reward reward) {

    RewardTaken rewardTaken = new RewardTaken();

    rewardTaken.setStatus(RewardTakenStatus.TAKEN.getValue());
    rewardTaken.setReward(reward);
    rewardTaken.setTakenDate(DateUtil.getTimeNow());
    rewardTaken.setOrdinalTime(DateUtil.getTimeNow());
    rewardTaken.setConsumer(consumer);
    rewardTaken.setRewardType(RewardType.POINT.getPhrase());
    String rewardPurchaseCode = rewardGlobalFunction.generateRewardPurchaseCode(
        consumer.getPsId(), reward);
    rewardTaken.setCode(rewardPurchaseCode);
    rewardTaken.setExpiredDate(reward.getExpiryOnDate());

    rewardTakenRepository.saveAndFlush(rewardTaken);

    return rewardTaken;
  }

  /**
   * Redeem Reward for Point Type .
   *
   * @param entity Form Data Request Body
   * @return Reward Taken Detail
   */

  @Transactional
  public RewardTaken getPointRewardRedemptionDetail(MultiValueMap<String, String> entity) {

    logger.info("REDEEM POINT REWARD SERVICE EXECUTED !");

    Long rewardTakenId = Long.valueOf(entity.getFirst("reward_taken_id"));

    RewardTaken rewardTaken = rewardTakenRepository
        .findOneByIdAndStatusForUpdate(rewardTakenId, RewardTakenStatus.TAKEN.getValue())
        .orElseThrow(() -> new RewardSystemException("Reward not found or already redeemed."));

    Consumer consumer = consumerService.getProfileByPsId(entity.getFirst("ps_id"));

    if (!Objects.equals(rewardTaken.getConsumer().getId(), consumer.getId())) {
      throw new RewardSystemException("You are not allowed to redeem this reward.");
    }

    rewardTakenService.buildRewardTakenForRedemption(entity, rewardTaken, consumer);
    rewardTakenRepository.saveAndFlush(rewardTaken);

    addCurrentBalanceForRedeemReward(entity, rewardTaken.getReward());

    return rewardTaken;
  }

  private void addCurrentBalanceForRedeemReward(MultiValueMap<String, String> entity,
      Reward reward) {
    final TrialBalanceParam addBalanceParam =
        rewardGlobalFunction.buildTrialBalanceParamForRedeemReward(entity.getFirst("ps_id"),
            entity.getFirst("client_id"), reward, reward.getId());

    trialBalanceService.credit(addBalanceParam);
  }

  public List<CampaignReward> getAllCampaignReward(Long campaignId, Integer page, Integer size) {

    logger.info("GET ALL CAMPAIGN REWARD SERVICE EXECUTED !");

    PageRequest pageRequest = new PageRequest(page - 1, size, Direction.DESC,
        "createdTime");

    return campaignRewardRepository.findAllByCampaignIdAndStatus(campaignId,
        RewardStatus.ACTIVE.getValue(), pageRequest);
  }
}