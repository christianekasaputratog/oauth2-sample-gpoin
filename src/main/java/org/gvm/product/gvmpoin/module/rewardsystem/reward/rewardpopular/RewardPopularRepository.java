package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RewardPopularRepository extends BaseRepository<RewardPopular, Long> {

  @Query("SELECT rp FROM RewardPopular rp WHERE rp.isActive = ?1 "
      + " AND rp.reward.remainStock != 0 AND rp.id IN (?2)"
      + " AND (CURRENT_DATE <= rp.reward.expiredDate AND (rp.reward.expiredDate IS NOT NULL)"
      + " OR rp.reward.expiredDate IS NULL) "
      + " AND (rp.reward.type = 'EGIFT' "
      + " OR rp.reward.type = 'EVENT' OR rp.reward.type = 'MERCHANDISE' "
      + " OR rp.reward.type = 'VOUCHER_CODE' OR rp.reward.type = 'POINT' "
      + " OR (rp.reward.type = 'PULSA' AND rp.reward.value in (SELECT svc.topUpDenom"
      + " FROM SepulsaVoucherCode svc WHERE svc.status = 'available')))")
  List<RewardPopular> findAllByIsActiveStatus(RewardPopularStatus rewardPopularStatus,
      Pageable pageable, List<Long> rewardPopularIds);

  @Query("SELECT rp FROM RewardPopular rp WHERE rp.reward.createdTime = ?")
  RewardPopular findOneByCreatedTime(Date createdTime);

  @Query("SELECT rp FROM RewardPopular rp WHERE rp.id IN (?1) ORDER BY rp.createdTime DESC")
  List<RewardPopular> findAllInIdsAndOrderByCreatedTimeDesc(List<Long> rewardPopularIds);

  @Query("SELECT rp FROM RewardPopular rp WHERE rp.reward.id = ?")
  Optional<RewardPopular> findOneByRewardId(Long rewardId);

  @Query("SELECT rp.reward.id FROM RewardPopular rp")
  List<Long> findAllRewardIdInRewardPopular();
}
