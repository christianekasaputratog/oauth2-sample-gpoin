package org.gvm.product.gvmpoin.module.campaign.campaignreward;

import java.util.List;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface CampaignRewardRepository extends BaseRepository<CampaignReward, Long> {

  @Query("SELECT cr FROM CampaignReward cr WHERE cr.rewardId.id = ?1")
  CampaignReward findOneByRewardId(Long rewardId);

  @Query("SELECT cr FROM CampaignReward cr WHERE cr.campaignId.id = ?1 AND cr.rewardId.status = ?2")
  List<CampaignReward> findAllByCampaignIdAndStatus(Long campaignId, Integer status,
      Pageable pageable);
}