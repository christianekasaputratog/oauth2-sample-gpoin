package org.gvm.product.gvmpoin.module.client;

import org.gvm.product.gvmpoin.module.common.BaseRepository;

import java.util.Optional;

public interface ClientRepository extends BaseRepository<Client, Long> {

  Optional<Client> findOneById(Long id);

  Optional<Client> findOneByClientId(String clientId);
}
