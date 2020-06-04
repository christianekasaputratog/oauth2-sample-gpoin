package org.gvm.product.gvmpoin.module.common.exception;

public class GlobalPoinException extends RuntimeException {

  private static final long serialVersionUID = -4143151938157003198L;

  public GlobalPoinException() {
  }

  public GlobalPoinException(String message) {
    super(message);
  }

  public GlobalPoinException(String message, Throwable cause) {
    super(message, cause);
  }

  public GlobalPoinException(Throwable cause) {
    super(cause);
  }

  public GlobalPoinException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
