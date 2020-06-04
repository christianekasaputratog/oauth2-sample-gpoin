package org.gvm.product.gvmpoin.module.rewardsystem.merchant;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
public interface MerchantRepository extends BaseRepository<Merchant, Long> {

  Page<Merchant> findAll(Pageable pageable);

  Merchant findOneByMerchantId(String merchantId);

  Merchant findOneByNameIgnoreCase(String merchantName);

}
