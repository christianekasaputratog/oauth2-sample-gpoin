package org.gvm.product.gvmpoin.module.rewardsystem.reward;

public enum RewardStatus {

  INACTIVE(0),
  ACTIVE(1),
  DELETE(2),
  EXPIRED(3),
  DRAFT(4);

  private final Integer value;

  RewardStatus(final Integer value) {
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }

}
