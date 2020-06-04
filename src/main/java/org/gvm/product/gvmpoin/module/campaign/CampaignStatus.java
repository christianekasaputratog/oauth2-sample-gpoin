package org.gvm.product.gvmpoin.module.campaign;

public enum CampaignStatus {
  INACTIVE(0, "Your campaign inactive"),
  ACTIVE(1, "Your campaign currently active"),
  EXPIRED(3, "Your campaign being expired"),
  CLOSED(4, "Your campaign being closed");

  private final int value;
  private final String message;

   CampaignStatus(int value, String message) {
    this.value = value;
    this.message = message;
  }

  public int value() {
    return this.value;
  }

  public String message() {
    return message;
  }
}