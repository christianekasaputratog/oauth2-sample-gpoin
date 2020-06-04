package org.gvm.product.gvmpoin.module.consumer;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConsumerRepository extends BaseRepository<Consumer, Long> {

  Optional<Consumer> findOneByPsId(String psId);

  Optional<Consumer> findOneByEmail(String email);

  Optional<Consumer> findOneByFacebookId(String facebookId);

  Optional<Consumer> findOneByTwitterId(String twitterId);

  @Query(value = "SELECT a FROM Consumer a WHERE a.emailVerificationCode = ?1")
  Optional<Consumer> findOneByEmailVerificationCode(String emailVerificationCode);

  Optional<Consumer> findTemporaryPasswordByPsId(String psId);

  @Query(value = "SELECT a FROM Consumer a WHERE a.psId IS NOT NULL ORDER BY a.id ASC")
  List<Consumer> findAllConsumerHasPsId();

  @Query(value = "SELECT a.status FROM Consumer a WHERE a.psId = ?1")
  Integer findConsumerStatusByPsId(String psId);

  @Query(value = "SELECT a  FROM Consumer a WHERE a.emailVerified IS NULL ORDER BY id ASC")
  List<Consumer> findAllConsumerHasEmailVerifiedNull();

  @Query(
      value = "SELECT * FROM consumer WHERE ps_id NOT IN(SELECT ps_id FROM talk_consumer "
          + " WHERE ps_id IS NOT NULL)",
      nativeQuery = true)
  List<Consumer> findAllConsumerHasPsIdButNotInWt();

  Consumer findOneByTrialBalanceId(Long trialBalanceId);

}
