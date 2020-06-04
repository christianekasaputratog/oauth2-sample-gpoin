package org.gvm.product.gvmpoin.module.rewardsystem;

import java.util.ArrayList;
import java.util.List;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.RewardRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopular;
import org.springframework.beans.factory.annotation.Autowired;

class StubRewardRepository {

  @Autowired
  RewardRepository rewardRepository;

  List<Reward> buildRewards() {
    Reward reward = new Reward();
    reward.setId(1L);

    Reward reward1 = new Reward();
    reward1.setId(2L);
    List<Reward> rewards = new ArrayList<>();
    rewards.add(reward);
    rewards.add(reward1);

    return rewards;
  }

  List<RewardPopular> buildRewardPopulars() {
    List<RewardPopular> rewardPopulars = new ArrayList<>();
    RewardPopular rewardPopular = new RewardPopular();
    rewardPopular.setId(1L);
    Reward reward = new Reward();
    reward.setId(2L);
    rewardPopular.setReward(reward);
    rewardPopulars.add(rewardPopular);

    RewardPopular rewardPopular1 = new RewardPopular();
    rewardPopular1.setId(2L);
    Reward reward1 = new Reward();
    reward1.setId(3L);
    rewardPopular1.setReward(reward1);
    rewardPopulars.add(rewardPopular1);
    return rewardPopulars;
  }

  Reward buildRewardById(Long rewardId) {
    Reward reward = new Reward();
    reward.setId(rewardId);
    return reward;
  }

}
