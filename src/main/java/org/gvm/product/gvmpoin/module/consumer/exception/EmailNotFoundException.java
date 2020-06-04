package org.gvm.product.gvmpoin.module.consumer.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class EmailNotFoundException extends GlobalPoinException {

  private static final long serialVersionUID = -8207781518185805693L;

  public EmailNotFoundException(String email) {
    super(String.format("Email %s not found/invalid!", email));
  }
}
