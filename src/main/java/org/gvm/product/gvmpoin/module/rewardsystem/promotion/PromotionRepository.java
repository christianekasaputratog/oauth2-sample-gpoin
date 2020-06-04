package org.gvm.product.gvmpoin.module.rewardsystem.promotion;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.gvm.product.gvmpoin.module.common.GlobalStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
public interface PromotionRepository extends BaseRepository<Promotion, Long> {

  @Query(value = "SELECT p FROM Promotion p WHERE p.status = :globalStatus  AND p.id IN :ids ")
  List<Promotion> findAllByStatusByIds(@Param("globalStatus") Integer globalStatus,
      Pageable pageable, @Param("ids") List<Long> ids);
}