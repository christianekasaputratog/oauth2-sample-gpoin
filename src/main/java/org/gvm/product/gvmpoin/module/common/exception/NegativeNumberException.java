package org.gvm.product.gvmpoin.module.common.exception;

public class NegativeNumberException extends GlobalPoinException {

  private static final long serialVersionUID = -8952185873238605298L;

  public NegativeNumberException() {
    super("Negative number not allowed");
  }
}
