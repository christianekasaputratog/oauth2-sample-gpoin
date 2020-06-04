package org.gvm.product.gvmpoin.module.rewardsystem.reward;

public enum RewardType {
  EGIFT("EGIFT"),
  EVENT("EVENT"),
  MERCHANDISE("MERCHANDISE"),
  PULSA("PULSA"),
  POINT("POINT"),
  VOUCHER_CODE("VOUCHER_CODE");

  private final String phrase;

  RewardType(String phrase) {
    this.phrase = phrase;
  }

  public String getPhrase() {
    return phrase;
  }
}