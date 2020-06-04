package org.gvm.product.gvmpoin.module.common.exception;

public class ExceededBalanceException extends GlobalPoinException {

  private static final long serialVersionUID = 1L;

  public ExceededBalanceException() {
    super("Your current balance is not sufficient");
  }
}
