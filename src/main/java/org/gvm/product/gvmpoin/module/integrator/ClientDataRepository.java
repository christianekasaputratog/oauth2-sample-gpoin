package org.gvm.product.gvmpoin.module.integrator;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClientDataRepository extends BaseRepository<ClientData, Long> {

  Page<ClientData> findByClient(String client, Pageable pageable);

  Optional<ClientData> findOneByEmail(String email);
}
