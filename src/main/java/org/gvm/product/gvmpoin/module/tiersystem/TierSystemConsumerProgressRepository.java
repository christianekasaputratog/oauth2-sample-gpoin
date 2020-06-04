package org.gvm.product.gvmpoin.module.tiersystem;

import org.gvm.product.gvmpoin.module.common.BaseRepository;

import java.util.Optional;

public interface TierSystemConsumerProgressRepository
    extends BaseRepository<TierSystemConsumerProgress, Long> {

  Optional<TierSystemConsumerProgress> findOneByTierSystemMasterIdAndConsumerId(
      Long tierSystemMasterId, Long consumerId);

}
