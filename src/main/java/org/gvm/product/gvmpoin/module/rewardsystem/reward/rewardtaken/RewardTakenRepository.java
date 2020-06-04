package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

public interface RewardTakenRepository extends BaseRepository<RewardTaken, Long> {

  @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
  @Query("SELECT rt FROM RewardTaken rt WHERE rt.id = ?1 AND rt.status = ?2")
  Optional<RewardTaken> findOneByIdAndStatusForUpdate(Long id, Integer status);

  @Query("SELECT rt FROM RewardTaken rt WHERE (( rt.rewardType = ?1 "
      + " AND CURRENT_DATE <= rt.expiredDate)"
      + " OR rt.status = ?2) AND rt.id = ?3")
  Optional<RewardTaken> findDetailById(String rewardType, Integer status, Long id);

  @Query("SELECT rt FROM RewardTaken rt WHERE (( rt.status = ?1 ) OR"
      + "(rt.status = ?2 AND rt.rewardType = ?3 )) "
      + " AND rt.consumer.id = ?4")
  List<RewardTaken> findNotExpiredRewardTakenByRewardTakenStatusAndConsumerId(
      Integer rewardTakenStatus, Integer status,
      String takenType, Long consumerId, Pageable pageable);

  @Query("SELECT rt FROM RewardTaken rt WHERE (( rt.status = ?1 "
      + "AND  rt.rewardType != ?2) OR rt.status = ?3) AND rt.consumer.id = ?4")
  List<RewardTaken> findExpiredRewardTakenByRewardTakenStatusAndConsumerId(
      Integer rewardTakenRedeemStatus, String rewardType,Integer rewardTakenExpiredStatus,
      Long consumerId, Pageable pageable);

  @Query("SELECT COUNT(rt) FROM RewardTaken rt WHERE rt.status = ?1 AND rt.consumer.psId = ?2")
  Integer countActiveRewardTaken(Integer rewardTakenStatus, String psId);

  @Query("SELECT rt FROM RewardTaken rt WHERE rt.status = ?1 AND rt.consumer.id = ?2")
  List<RewardTaken> findAllByStatusAndConsumerId(Integer rewardTakenStatus, Long consumerId);
}