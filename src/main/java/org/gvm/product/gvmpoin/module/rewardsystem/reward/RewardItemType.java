package org.gvm.product.gvmpoin.module.rewardsystem.reward;

public enum RewardItemType {

  NON_PHYSICAL("NON PHYSICAL", 0),
  PHYSICAL("PHYSICAL", 1);

  private final String phrase;

  private final Integer value;

  RewardItemType(final String phrase, final Integer value) {
    this.phrase = phrase;
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }

  @Override
  public String toString() {
    return phrase;
  }
}
