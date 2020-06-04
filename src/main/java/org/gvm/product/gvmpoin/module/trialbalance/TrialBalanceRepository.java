package org.gvm.product.gvmpoin.module.trialbalance;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import javax.persistence.LockModeType;

public interface TrialBalanceRepository extends BaseRepository<TrialBalance, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(value = "SELECT a FROM TrialBalance a WHERE a.id = ?1")
  Optional<TrialBalance> findTrialBalanceForUpdate(Long balanceId);
}
