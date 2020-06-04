package org.gvm.product.gvmpoin.module.rewardsystem.partner;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
public interface PartnerRepository extends BaseRepository<Partner, Long> {

  @Query("SELECT p FROM Partner p WHERE p.client.status = ?1 ")
  List<Partner> findAllByStatus(@Param("status") Integer status);

  @Query("SELECT p FROM Partner p WHERE p.client.clientId =?1")
  Optional<Partner> findOneByClientId(String clientId);
}
