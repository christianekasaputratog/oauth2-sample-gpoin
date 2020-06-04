package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import javax.persistence.LockModeType;

public interface SepulsaVoucherRepository extends BaseRepository<SepulsaVoucherCode, Long> {

  @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
  @Query("SELECT svc FROM SepulsaVoucherCode svc WHERE svc.code = ?1 AND svc.status = ?2")
  Optional<SepulsaVoucherCode> findOneByCodeAndStatusForUpdate(String code, String status);

  @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
  @Query("SELECT svc FROM SepulsaVoucherCode svc WHERE svc.status LIKE ?1 and svc.topUpDenom = ?2")
  Page<SepulsaVoucherCode> findOneByStatusAndTopUpDenomForUpdate(String status, Integer topUpDenom,
      Pageable pageable);

  Optional<SepulsaVoucherCode> findOneByTransactionId(String transactionId);

  @Query("SELECT svc.mobileNumber FROM SepulsaVoucherCode svc WHERE svc.code = ?1")
  String findMobileNumberByVoucherCode(String code);
}