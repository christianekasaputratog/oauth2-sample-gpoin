package org.gvm.product.gvmpoin.module.consumer;

public enum ConsumerLoginStatus {

  ALREADY_LOGIN(1),
  LOGOUT(2),
  DOESNT_HAS_PIN(3),
  EMAIL_NOT_VERIFIED(4),
  FORGOT_PIN(5);

  private int value;

  ConsumerLoginStatus(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

}
