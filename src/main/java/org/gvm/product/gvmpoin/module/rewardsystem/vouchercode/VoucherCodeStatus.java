package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode;

public enum VoucherCodeStatus {

  STATUS_REDEEMED("redeemed"),
  STATUS_AVAILABLE("available"),
  STATUS_TAKEN("taken");

  private final String phrase;

  VoucherCodeStatus(final String phrase) {
    this.phrase = phrase;
  }

  public String getPhrase() {
    return phrase;
  }

}
