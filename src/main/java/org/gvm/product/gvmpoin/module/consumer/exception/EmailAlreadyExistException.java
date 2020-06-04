package org.gvm.product.gvmpoin.module.consumer.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class EmailAlreadyExistException extends GlobalPoinException {
  private static final long serialVersionUID = -1087531628295394712L;

  public EmailAlreadyExistException(String email) {
    super(String.format("Email %s already exist.", email));
  }
}
