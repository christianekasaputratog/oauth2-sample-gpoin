package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken;

public enum SocialMediaType {

  DEFAULT("", 0),
  FACEBOOK("FACEBOOK", 1),
  INSTAGRAM("INSTAGRAM", 2),
  TWITTER("TWITTER", 3);

  private final String phrase;

  private final Integer value;

  SocialMediaType(final String phrase, final Integer value) {
    this.phrase = phrase;
    this.value = value;
  }

  @Override
  public String toString() {
    return phrase;
  }

  public Integer getValue() {
    return value;
  }
}