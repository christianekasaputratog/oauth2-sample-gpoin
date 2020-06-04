package org.gvm.product.gvmpoin.module.rewardsystem.promotion;

import org.gvm.product.gvmpoin.module.client.exception.ClientNotFoundException;
import org.gvm.product.gvmpoin.module.common.GlobalStatus;
import org.gvm.product.gvmpoin.module.rewardsystem.partner.Partner;
import org.gvm.product.gvmpoin.module.rewardsystem.partner.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
@Service
public class PromotionService {

  private PromotionRepository promotionRepository;
  private PartnerRepository partnerRepository;
  private PromotionPartnerRepository promotionPartnerRepository;

  @Autowired
  public PromotionService(PromotionRepository promotionRepository,
      PartnerRepository partnerRepository, PromotionPartnerRepository promotionPartnerRepository) {
    this.promotionRepository = promotionRepository;
    this.partnerRepository = partnerRepository;
    this.promotionPartnerRepository = promotionPartnerRepository;
  }

  List<Promotion> getActivePromotions(int pageNumber, int size, String clientId) {

    PageRequest pageRequest = new PageRequest(pageNumber - 1, size, Sort.Direction.DESC,
        "createdTime");

    return getAllPromotionByPartner(clientId, pageRequest);
  }

  private List<Promotion> getAllPromotionByPartner(String clientId, PageRequest pageRequest) {

    Partner partner = partnerRepository.findOneByClientId(clientId)
        .orElseThrow(() -> new ClientNotFoundException(clientId));

    List<PromotionPartner> promotionPartners = promotionPartnerRepository
        .findAllByPartnerId(partner.getId());

    List<Long> promotionIds = new ArrayList<>();

    Long defaultPromotionIds = 0L;

    if (promotionPartners.isEmpty()) {
      promotionIds.add(defaultPromotionIds);
    } else {
      for (PromotionPartner promotionPartner : promotionPartners) {
        promotionIds.add(promotionPartner.getPromotion().getId());
      }
    }
    return promotionRepository.findAllByStatusByIds(GlobalStatus.ACTIVE.getValue(), pageRequest,
        promotionIds);
  }
}