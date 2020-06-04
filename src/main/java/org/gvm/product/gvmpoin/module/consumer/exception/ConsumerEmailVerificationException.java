package org.gvm.product.gvmpoin.module.consumer.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class ConsumerEmailVerificationException extends GlobalPoinException {

  public ConsumerEmailVerificationException() {
    super("Invalid or expired email verification code");
  }
}
