package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken;

public enum RewardTakenStatus {

  TAKEN(0),
  REDEEMED(1),
  EXPIRED(2),
  AVAILABLE(3);

  private final Integer value;

  RewardTakenStatus(final Integer value) {
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }

}
