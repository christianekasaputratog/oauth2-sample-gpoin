package org.gvm.product.gvmpoin.module.consumer.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class PasswordException extends GlobalPoinException {

  private static final long serialVersionUID = -8207781518185805693L;

  public PasswordException() {
    super("Password doesn't match !");
  }

  public PasswordException(String message) {
    super(message);
  }

}
