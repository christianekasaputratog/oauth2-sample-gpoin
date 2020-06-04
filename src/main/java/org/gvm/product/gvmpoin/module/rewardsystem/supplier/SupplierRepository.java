package org.gvm.product.gvmpoin.module.rewardsystem.supplier;

import org.gvm.product.gvmpoin.module.common.BaseRepository;

import java.util.Optional;

public interface SupplierRepository extends BaseRepository<Supplier, Long> {

  Optional<Supplier> findOneById(Long supplierId);

  Supplier findOneByNameIgnoreCase(String supplierName);

}
