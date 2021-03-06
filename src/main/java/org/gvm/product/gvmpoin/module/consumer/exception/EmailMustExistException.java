package org.gvm.product.gvmpoin.module.consumer.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class EmailMustExistException extends GlobalPoinException {

  private static final long serialVersionUID = -8207781518185805693L;

  public EmailMustExistException() {
    super("Email must exist!");
  }
}
