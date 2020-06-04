package org.gvm.product.gvmpoin.module.consumer.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class ConsumerAccessDeniedException extends GlobalPoinException {

  public ConsumerAccessDeniedException(String message) {
    super(message);
  }
}
