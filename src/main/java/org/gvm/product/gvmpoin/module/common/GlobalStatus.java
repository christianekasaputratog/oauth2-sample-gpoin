package org.gvm.product.gvmpoin.module.common;

public enum GlobalStatus {

  ACTIVE("ACTIVE", 1);

  private final String phrase;

  private final Integer value;

  GlobalStatus(final String phrase, final Integer value) {
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
