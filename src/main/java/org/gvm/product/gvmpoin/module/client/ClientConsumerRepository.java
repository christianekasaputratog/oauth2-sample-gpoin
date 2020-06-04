package org.gvm.product.gvmpoin.module.client;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.gvm.product.gvmpoin.module.consumer.ClientConsumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClientConsumerRepository extends BaseRepository<ClientConsumer, String> {

  Page<ClientConsumer> findAllByRegisterFrom(String registerFrom, Pageable pageable);

  Optional<ClientConsumer> findOneByPsId(String psId);
}
