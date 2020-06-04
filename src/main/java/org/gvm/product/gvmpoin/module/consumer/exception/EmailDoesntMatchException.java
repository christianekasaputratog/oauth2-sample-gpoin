package org.gvm.product.gvmpoin.module.consumer.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class EmailDoesntMatchException extends GlobalPoinException {

  private static final long serialVersionUID = -7940570905669819585L;

  public EmailDoesntMatchException(String email) {
    super(String.format("Email %s doesnt match with email in poin system", email));
  }

}
