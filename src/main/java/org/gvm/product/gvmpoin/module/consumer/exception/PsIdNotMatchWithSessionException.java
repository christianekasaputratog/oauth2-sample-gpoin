package org.gvm.product.gvmpoin.module.consumer.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class PsIdNotMatchWithSessionException extends GlobalPoinException {

  public PsIdNotMatchWithSessionException() {
    super("PS ID not match with the session. Login again");
  }
}
