package org.gvm.product.gvmpoin.module.continuousengagement;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MyProgressRepository extends BaseRepository<MyProgress, Long> {

  @Query(value = "SELECT a FROM MyProgress a WHERE a.progressbar.id = :progressbarId "
      + " AND a.consumer.id = :consumerId")
  Optional<MyProgress> findOneByProgressbarIdandConsumerId(
      @Param("progressbarId") Long progressbarId, @Param("consumerId") Long consumerId);

  @Query(value = "SELECT a FROM MyProgress a WHERE a.progressbar.id = :progressbarId "
      + " AND a.consumer.psId = :psId")
  Optional<MyProgress> findOneByProgressbarIdandPsId(@Param("progressbarId") Long progressbarId,
      @Param("psId") String psId);

  @Query(value = "SELECT m FROM MyProgress m where m.id NOT IN (:notInIds) "
      + " AND m.closingBalance > 0")
  List<MyProgress> findAllByIdNotInAndClosingBalanceGreaterThanZero(
      @Param("notInIds") List<Long> notInIds);

  @Query(value = "SELECT m FROM MyProgress m where m.closingBalance > 0")
  List<MyProgress> findAllByClosingBalanceGreaterThanZero();
}
