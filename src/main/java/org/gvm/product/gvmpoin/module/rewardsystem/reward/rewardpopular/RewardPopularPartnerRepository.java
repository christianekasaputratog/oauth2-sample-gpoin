package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RewardPopularPartnerRepository extends BaseRepository<RewardPopularPartner, Long> {

  @Query("SELECT rpp FROM RewardPopularPartner rpp WHERE rpp.partnerId =?1")
  List<RewardPopularPartner> findRewardPopularPartnerByPartnerId(Long partnerId);

  @Query("SELECT rpp FROM RewardPopularPartner rpp WHERE rpp.rewardPopularId =?1")
  List<RewardPopularPartner> findRewardPopularPartnerByRewardPopularId(Long rewardPopularId);

  @Query("SELECT rpp FROM RewardPopularPartner rpp WHERE rpp.partnerId =?1 AND"
      + " rpp.rewardPopularId =?2")
  RewardPopularPartner findOneByPartnerIdRewardPopularId(Long partnerId, Long rewardPopularId);
}
