package org.gvm.product.gvmpoin.module.campaign.leaderboard;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeaderboardTransactionLogRepository
    extends BaseRepository<LeaderboardTransactionLog, Long> {

  List<LeaderboardTransactionLog> findByLeaderboardId(Long leaderboardId);

  Optional<LeaderboardTransactionLog> findOneById(Long id);

  Optional<LeaderboardTransactionLog> findOneByClientTransactionId(String clientTransactionId);

  @Query(value = "SELECT COUNT(a.id) FROM LeaderboardTransactionLog a "
      + " WHERE a.clientTransactionId = :ctid AND a.activity = 'ROLLBACK'")
  Long countRollbackTransaction(@Param("ctid") String clientTransactionId);
}
