package org.gvm.product.gvmpoin.module.campaign;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignChildRepository extends BaseRepository<CampaignChild, Long> {

  @Query("SELECT cc FROM CampaignChild cc WHERE cc.campaignChild.campaignUniqueCode = ?1")
  CampaignChild findOneByCampaignChildUniqueCode(String campaignUniqueCode);
}