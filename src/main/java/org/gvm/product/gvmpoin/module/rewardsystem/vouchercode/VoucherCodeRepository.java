package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.gvm.product.gvmpoin.module.rewardsystem.supplier.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import javax.persistence.LockModeType;

public interface VoucherCodeRepository extends BaseRepository<VoucherCode, Long> {

  @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
  @Query("SELECT vc FROM VoucherCode vc WHERE vc.status LIKE ?1 and vc.topUpDenom = ?2 "
      + "and vc.supplier = ?3")
  Page<VoucherCode> findOneByStatusAndTopUpDenomAndSupplier(String status,
      Integer topUpDenom, Supplier supplier, Pageable pageable);

  @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
  @Query("SELECT vc FROM VoucherCode vc WHERE vc.code = ?1 AND vc.status = ?2")
  Optional<VoucherCode> findOneByCodeAndStatusForUpdate(String code, String status);

}
