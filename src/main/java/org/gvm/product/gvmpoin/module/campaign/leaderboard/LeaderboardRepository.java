package org.gvm.product.gvmpoin.module.campaign.leaderboard;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import javax.persistence.LockModeType;

public interface LeaderboardRepository extends BaseRepository<Leaderboard, Long> {

  @Query("SELECT l FROM Leaderboard l WHERE l.campaign.campaignUniqueCode = ?1 AND l.psId = ?2")
  Leaderboard findOneByCampaignUniqueCodeAndPsId(String campaignUniqueCode, String psId);

  @Query("SELECT l FROM Leaderboard l WHERE l.campaign.campaignUniqueCode = ?1 AND"
      + " l.campaign.clientId =?2")
  List<Leaderboard> findAllByCampaignUniqueCodeAndClient(String campaignUniqueCode, String clientId,
      Pageable pageable);

  @Query(value = "SELECT COUNT(l) FROM Leaderboard l WHERE l.campaign.campaignUniqueCode = ?1")
  Long countTotalCampaignParticipants(String campaignUniqueCode);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(value = "SELECT l FROM Leaderboard l WHERE l.campaign.campaignUniqueCode = ?1 "
      + " ORDER BY l.closingBalance DESC, l.lastUpdatedTime ASC")
  List<Leaderboard> findAllForUpdateRank(String campaignUniqueCode);
}
