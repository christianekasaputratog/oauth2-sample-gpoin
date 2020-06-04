package org.gvm.product.gvmpoin.module.continuousengagement;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface MyProgressLogRepository extends BaseRepository<MyProgressLog, Long> {

  @Query("SELECT DISTINCT m.myProgressId from MyProgressLog m WHERE m.transactionTime "
      + " BETWEEN :startDate AND :endDate AND m.credit > 0")
  List<Long> findDistinctMyProgressIdByTransactionTimeBetweenAndCreditGreaterThanZero(
      @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
