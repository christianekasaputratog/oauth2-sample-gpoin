package org.gvm.product.gvmpoin.module.rewardsystem.promotion;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromotionPartnerRepository extends BaseRepository<PromotionPartner, Long> {

  @Query("SELECT pp from PromotionPartner pp WHERE pp.partner.id =?1")
  List<PromotionPartner> findAllByPartnerId(Long partnerId);
}