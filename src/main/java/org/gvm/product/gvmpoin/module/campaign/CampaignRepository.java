package org.gvm.product.gvmpoin.module.campaign;

import java.util.List;
import java.util.Optional;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface CampaignRepository extends BaseRepository<Campaign, Long> {

  @Query(value = "SELECT c FROM Campaign c WHERE c.campaignUniqueCode = ?1 AND"
      + " c.clientId = ?2")
  Optional<Campaign> findOneByCampaignUniqueCode(String campaignUniqueCode, String clientId);

  @Query("SELECT c FROM Campaign c WHERE c.clientId.clientId =?1 AND c.status =?2")
  List<Campaign> findAllByClientIdAndStatus(String clientId, Integer status, Pageable pageable);
}