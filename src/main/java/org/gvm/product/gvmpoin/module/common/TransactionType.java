package org.gvm.product.gvmpoin.module.common;

public enum TransactionType {

  CREDIT("CREDIT", 1),
  DEBIT("DEBIT", 2);

  private final String phrase;

  private final Integer value;

  TransactionType(final String phrase, final Integer value) {
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