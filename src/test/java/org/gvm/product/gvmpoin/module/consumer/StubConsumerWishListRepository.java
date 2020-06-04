package org.gvm.product.gvmpoin.module.consumer;

import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.consumer.wishlist.ConsumerWishList;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;

public class StubConsumerWishListRepository {

  public static  Reward reward;

  public static Consumer consumer;

  public  static Client client;

  public static ConsumerWishList buildOptionalConsumerWishList() {
    ConsumerWishList consumerWishList = new ConsumerWishList();
    consumerWishList.setReward(reward);
    consumerWishList.setConsumer(consumer);
    consumerWishList.setClient(client);

    return consumerWishList;
  }
}