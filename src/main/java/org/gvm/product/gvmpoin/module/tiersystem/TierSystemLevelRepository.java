package org.gvm.product.gvmpoin.module.tiersystem;

import org.gvm.product.gvmpoin.module.common.BaseRepository;

import java.util.List;

public interface TierSystemLevelRepository extends BaseRepository<TierSystemLevel, Long> {

  public TierSystemLevel findFirstByTierSystemMasterIdOrderByLevelAsc(Long tierSystemMasterId);

  public List<TierSystemLevel> findByTierSystemMasterIdOrderByLevelAsc(Long tierSystemMasterId);
}
