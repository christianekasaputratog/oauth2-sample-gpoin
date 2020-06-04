package org.gvm.product.gvmpoin.module.rewardsystem;

import java.util.Optional;
import org.gvm.product.gvmpoin.module.rewardsystem.partner.Partner;

class StubPartnerRepository {

  static Optional<Partner> buildOptionalPartner() {
    Partner partner = new Partner();
    partner.setId(2L);
    return Optional.of(partner);
  }
}
