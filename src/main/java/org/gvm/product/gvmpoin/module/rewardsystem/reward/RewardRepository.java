package org.gvm.product.gvmpoin.module.rewardsystem.reward;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
public interface RewardRepository extends BaseRepository<Reward, Long> {

  Page<Reward> findAll(Pageable pageable);

  @Query("SELECT r FROM Reward r WHERE r.category.id = ?1 AND r.remainStock !=0")
  Page<Reward> findByCategoryId(Long categoryId, Pageable pageable);

  @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
  @Query("SELECT r FROM Reward r WHERE r.id = ?1 ")
  Optional<Reward> findOneByIdForUpdate(Long id);

  @Query("SELECT r FROM Reward r WHERE r.id = ?1 AND r.remainStock !=0")
  Optional<Reward> findOneById(Long id);

  @Query("SELECT r FROM Reward r WHERE r.status = ?1 AND r.remainStock !=0"
      + " ORDER BY createdTime DESC")
  List<Reward> findTop5ByStatusOrderByCreatedTimeDesc(Integer rewardStatus, Pageable pageable);

  @Query("SELECT r FROM Reward r WHERE r.type LIKE :type ")
  List<Reward> findAllRewardByType(@Param("type") RewardType type);

  @Query("SELECT r FROM Reward r WHERE r.status = ?1 "
      + " AND CURRENT_DATE <= r.expiredDate "
      + " AND reward_system_category_id = ?2 "
      + " AND r.remainStock != 0")
  List<Reward> findNotExpiredRewardByRewardStatusAndCategoryId(Integer rewardStatus,
      Long categoryId, Pageable pageable);

  @Query("SELECT r FROM Reward r WHERE r.status = ?1 "
      + " AND CURRENT_DATE <= r.expiredDate "
      + " AND r.remainStock != 0"
      + "AND r.merchant.id = ?2 ")
  List<Reward> findNotExpiredRewardByRewardStatusAndMerchantId(Integer rewardStatus,
      Long merchantId, Pageable pageable);

  @Query("SELECT r FROM Reward r WHERE r.id NOT IN (:rewardIds) AND r.status = (:rewardStatus)"
      + "AND r.remainStock != 0")
  List<Reward> findAllByExcludeIdsAndStatus(@Param("rewardIds") List<Long> rewardIds,
      @Param("rewardStatus") Integer rewardStatus, Pageable pageable);

  @Query("SELECT r FROM Reward r WHERE r.status = ?1 AND r.remainStock != 0")
  List<Reward> findAllByStatus(Integer rewardStatus);

  @Query("SELECT r FROM Reward r WHERE r.id = :rewardIds AND r.status = (:rewardStatus) "
      + "AND r.remainStock != 0")
  Reward findByIdsAndStatus(@Param("rewardIds") Long rewardId,
      @Param("rewardStatus") Integer rewardStatus);

  @Query("SELECT r FROM Reward r WHERE r.id = :rewardIds AND r.status = (:rewardStatus) "
      + "AND r.remainStock != 0 AND r.id NOT IN :ids")
  Reward findByIdsAndStatusAndNotInIds(@Param("rewardIds") Long rewardId,
      @Param("rewardStatus") Integer rewardStatus, @Param("ids") List<Long> ids);

  @Query("SELECT r FROM Reward r WHERE r.status = :rewardStatus AND r.remainStock !=0"
      + " AND r.id NOT IN (:ids) ORDER BY r.createdTime DESC")
  List<Reward> findAllByStatusOrderByCreatedTimeDesc(@Param("rewardStatus") Integer rewardStatus,
      @Param("ids") List<Long> ids);

  @Query("SELECT r FROM Reward r WHERE r.createdTime = ?1")
  Reward findOneByCreatedTime(Date createdTime);
}