package org.gvm.product.gvmpoin.module.rewardsystem.partner;

import org.gvm.product.gvmpoin.module.common.GlobalStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
@Service
public class PartnerService {

  private PartnerRepository partnerRepository;

  @Autowired
  public PartnerService(PartnerRepository partnerRepository) {
    this.partnerRepository = partnerRepository;
  }

  List<Partner> getAllForClient() {
    return partnerRepository.findAllByStatus(GlobalStatus.ACTIVE.getValue());
  }
}
