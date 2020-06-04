package org.gvm.product.gvmpoin.module.consumer;

public enum ConsumerActivity {

  FORGOT_PIN("FORGOT_PIN"),
  GENERATE_NEW_PIN("GENERATE_NEW_PIN");

  private String value;

  ConsumerActivity(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
