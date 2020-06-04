package org.gvm.product.gvmpoin.module.rewardsystem.reward;

import org.gvm.product.gvmpoin.module.campaign.CampaignService;
import org.gvm.product.gvmpoin.module.campaign.campaignreward.CampaignReward;
import org.gvm.product.gvmpoin.module.campaign.campaignreward.CampaignRewardRepository;
import org.gvm.product.gvmpoin.module.campaign.leaderboard.LeaderboardParam;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemConfig;
import org.gvm.product.gvmpoin.module.rewardsystem.RewardSystemException;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardSocialMedia;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenDetail;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken.RewardTakenDetailRepository;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceParam;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceService;
import org.gvm.product.gvmpoin.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.sql.Timestamp;

@Service
public class RewardGlobalFunction {

  @Autowired
  RewardTakenDetailRepository rewardTakenDetailRepository;

  @Autowired
  TrialBalanceService trialBalanceService;

  @Autowired
  CampaignRewardRepository campaignRewardRepository;

  @Autowired
  CampaignService campaignService;

  public RewardSystemException throwExceptionWhenRewardNotFoundOrAlreadyTaken() {
    return new RewardSystemException("Reward not found or already taken.");
  }

  /**
   * Check Reward Stock .
   *
   * @param reward Reward Detail
   */
  public void throwExceptionWhenRewardStockIsEmpty(Reward reward) {
    if (reward.getRemainStock() == 0) {
      throw new RewardSystemException("Reward is out of stock.");
    }
  }

  /**
   * Check Expiration of Reward .
   *
   * @param reward Reward Detail
   */
  public void throwExceptionWhenRewardExpired(Reward reward) {
    if (reward != null && reward.getExpiredDate().getTime() <= DateUtil.getTimeNow().getTime()) {
      throw new RewardSystemException("Reward has already expired.");
    }
  }

  /**
   * Build Trial Balance Param for Redeem Reward .
   *
   * @param psId GPoin Unique ID
   * @param clientId Client ID
   * @param reward Reward Detail
   * @param rewardId Reward ID
   * @return TrialBalanceParam Model
   */
  public TrialBalanceParam buildTrialBalanceParamForRedeemReward(String psId,
      String clientId, Reward reward, Long rewardId) {
    return new TrialBalanceParam
        .Builder(psId, null, reward.getPointValue(), RewardSystemConfig.ACTIVITY_REDEEM,
        RewardSystemConfig.ACTIVITY_OBJECT_REWARD, clientId)
        .description(RewardSystemConfig.DESCRIPTION_REDEEM_REWARD)
        .objectId(rewardId)
        .password(null)
        .build();
  }

  /**
   * Subtract Point By Campaign Condition .
   *
   * @param clientId Client ID
   * @param reward Reward Detail
   * @param consumer Consumer Detail
   */
  public void subtractPointByCampaignCondition(Reward reward, String clientId, Consumer consumer) {
    if (reward.getIsCampaignReward() == 0) {
      final TrialBalanceParam substractBalanceParam = buildTrialBalanceParamForTakeReward(consumer,
          clientId, reward);
      trialBalanceService.debit(substractBalanceParam);
    } else {
      CampaignReward campaignReward = campaignRewardRepository.findOneByRewardId(reward.getId());
      final LeaderboardParam subtractLeaderboardParam = buildLeaderboardParamForTakeReward(consumer,
          campaignReward.getCampaignId().getCampaignUniqueCode(), reward.getPointCost(),
          reward);
      campaignService.getLeaderboarWithReducedValue(subtractLeaderboardParam);
    }
  }

  /**
   * Build Trial Balance Param for Take Reward .
   *
   * @param consumer GPoin Member
   * @param clientId Client Id
   * @param reward Detail Reward request
   * @return Trial Balance Param
   */
  public TrialBalanceParam buildTrialBalanceParamForTakeReward(Consumer consumer, String clientId,
      Reward reward) {
    return new TrialBalanceParam
        .Builder(consumer.getPsId(), null, reward.getPointCost(),
        RewardSystemConfig.ACTIVITY_TAKE, RewardSystemConfig.ACTIVITY_OBJECT_REWARD, clientId)
        .description(RewardSystemConfig.DESCRIPTION_TAKE_REWARD)
        .objectId(reward.getId())
        .password(consumer.getPassword())
        .build();
  }

  public LeaderboardParam buildLeaderboardParamForTakeReward(Consumer consumer,
      String campaignUniqueCode, Integer score, Reward reward) {
    return new LeaderboardParam.Builder()
        .description(RewardSystemConfig.DESCRIPTION_TAKE_REWARD)
        .objectId(reward.getId())
        .psId(consumer.getPsId())
        .campaignUniqueCode(campaignUniqueCode)
        .score(score)
        .activity(RewardSystemConfig.ACTIVITY_TAKE)
        .activityObject(RewardSystemConfig.ACTIVITY_OBJECT_REWARD)
        .build();
  }

  /**
   * Generate Reward Purchase Code for Event and Merchandise Reward .
   *
   * @param psId GPoin Unique ID
   * @param reward Reward Detail
   * @return Reward Generated Code
   */
  public String generateRewardPurchaseCode(String psId, Reward reward) {

    String merchant = reward.getMerchant().getName().substring(0, 3);
    merchant = merchant.replace(" ", "").toLowerCase();

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    return merchant + psId + timestamp.getTime();
  }

  public void throwExceptionWhenRewardAlreadyExpired(Reward reward) {
    if (reward.getExpiredDate() != null && reward.getExpiredDate().getTime() <= DateUtil
        .getTimeNow().getTime()) {
      throw new RewardSystemException("Reward has already expired.");
    }
  }

  public RewardTakenDetail buildRewardTakenDetail(MultiValueMap<String, String> entity,
      Consumer consumer) {
    RewardTakenDetail rewardTakenDetail = new RewardTakenDetail();

    rewardTakenDetail.setRecipientName(entity.getFirst("recipient_name").replaceAll("<[^>]*>", ""));
    rewardTakenDetail.setPhoneNumber(entity.getFirst("phone_number").replaceAll("<[^>]*>", ""));
    rewardTakenDetail.setAddress(entity.getFirst("address").replaceAll("<[^>]*>", ""));
    rewardTakenDetail.setPostCode(entity.getFirst("post_code").replaceAll("<[^>]*>", ""));
    rewardTakenDetail.setCity(entity.getFirst("city").replaceAll("<[^>]*>", ""));
    rewardTakenDetail
        .setAdditionalInfo(entity.getFirst("additional_info").replaceAll("<[^>]*>", ""));
    if (consumer.getConsumerSocialMedia() != null) {
      setRewardTakenDetailSocialMedia(consumer, rewardTakenDetail);
      rewardTakenDetail.setRewardSocialMedia(buildRewardSocialMedia(consumer));
    }
    return rewardTakenDetailRepository.save(rewardTakenDetail);
  }

  private void setRewardTakenDetailSocialMedia(Consumer consumer,
      RewardTakenDetail rewardTakenDetailSocialMedia) {
        rewardTakenDetailSocialMedia.setFacebookAccount(consumer.getConsumerSocialMedia()
            .getFacebookAccount());
        rewardTakenDetailSocialMedia.setTwitterAccount(consumer.getConsumerSocialMedia()
            .getTwitterAccount());
      rewardTakenDetailSocialMedia.setInstagramAccount(consumer.getConsumerSocialMedia()
          .getInstagramAccount());
  }

  private RewardSocialMedia buildRewardSocialMedia(Consumer consumer) {
    RewardSocialMedia rewardSocialMedia = new RewardSocialMedia();
      rewardSocialMedia.setFacebookAccount(consumer.getConsumerSocialMedia().getFacebookAccount());
    rewardSocialMedia.setTwitterAccount(consumer.getConsumerSocialMedia().getTwitterAccount());
    rewardSocialMedia.setInstagramAccount(consumer.getConsumerSocialMedia().getInstagramAccount());

    return rewardSocialMedia;
  }
}