package org.gvm.product.gvmpoin.module.rewardsystem;

import java.util.ArrayList;
import java.util.List;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular.RewardPopularPartner;

public class StubRewardPopularPartnerRepository {

  public static List<RewardPopularPartner> buildRewardPopularPartners(){
    RewardPopularPartner rewardPopularPartner = new RewardPopularPartner();
    rewardPopularPartner.setPartnerId(2L);
    rewardPopularPartner.setRewardPopularId(2L);

    RewardPopularPartner rewardPopularPartner2 = new RewardPopularPartner();
    rewardPopularPartner.setPartnerId(3L);
    rewardPopularPartner.setRewardPopularId(3L);

    List<RewardPopularPartner> rewardPopularPartners = new ArrayList<>();
    rewardPopularPartners.add(rewardPopularPartner);
    rewardPopularPartners.add(rewardPopularPartner2);

    return rewardPopularPartners;

  }

}
