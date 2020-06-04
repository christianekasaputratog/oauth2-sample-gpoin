package org.gvm.product.gvmpoin.module.continuousengagement.progressbar;

import org.gvm.product.gvmpoin.module.common.BaseRepository;

import java.util.Optional;

public interface ProgressbarRepository extends BaseRepository<Progressbar, Long> {

  Optional<Progressbar> findOneById(Long progressbarId);
}
