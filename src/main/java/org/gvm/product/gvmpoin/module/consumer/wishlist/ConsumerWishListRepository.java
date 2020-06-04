package org.gvm.product.gvmpoin.module.consumer.wishlist;

import org.gvm.product.gvmpoin.module.common.BaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConsumerWishListRepository extends BaseRepository<ConsumerWishList, Long> {

  @Query("SELECT cwl FROM ConsumerWishList cwl WHERE cwl.reward.id = ?1 AND cwl.consumer.id = ?2 "
      + "AND cwl.client.clientId = ?3")
  ConsumerWishList findOneByRewardIdAndConsumerIdAndClientId(Long rewardId, Long consumerId,
      String clientId);

  @Query("SELECT cwl FROM ConsumerWishList cwl WHERE cwl.consumer.id = :consumerId "
      + "AND cwl.reward.status = :status AND cwl.reward.remainStock != 0 AND "
      + "cwl.client.clientId = :clientId")
  List<ConsumerWishList> findAllByConsumerIdAndRewardStatusAndClientId(
      @Param("consumerId") Long consumerId, @Param("status") Integer status,
      @Param("clientId") String clientId, Pageable pageable);

  @Query("SELECT cwl FROM ConsumerWishList cwl WHERE cwl.reward.id = :rewardId")
  List<ConsumerWishList> findAllByRewardId(@Param("rewardId") Long rewardId);
}