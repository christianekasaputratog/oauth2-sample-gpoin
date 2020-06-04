package org.gvm.product.gvmpoin.module.consumer.client;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsumerClientRepository extends BaseRepository<ConsumerClient, Long> {

  @Query("SELECT cc FROM ConsumerClient cc WHERE cc.consumer.psId = ?1 AND cc.client.clientId = ?2")
  Optional<ConsumerClient> findOneByPsIdAndClientId(String psId, String clientId);
}