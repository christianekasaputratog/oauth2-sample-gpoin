package org.gvm.product.gvmpoin.module.tiersystem;

import org.gvm.product.gvmpoin.module.common.BaseRepository;

import java.util.Optional;

public interface TierSystemMasterRepository extends BaseRepository<TierSystemMaster, Long> {

  public Optional<TierSystemMaster> findOneByClientClientId(String clientId);
}
