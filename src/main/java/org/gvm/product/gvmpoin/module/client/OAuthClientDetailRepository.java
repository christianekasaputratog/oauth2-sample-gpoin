package org.gvm.product.gvmpoin.module.client;

import org.gvm.product.gvmpoin.module.common.BaseRepository;

import java.util.Optional;

public interface OAuthClientDetailRepository extends BaseRepository<OauthClientDetail, Long> {

  Optional<OauthClientDetail> findOneByClientId(String clientId);
}
