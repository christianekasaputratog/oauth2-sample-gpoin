package org.gvm.product.gvmpoin.module.client.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class ClientNotFoundException extends GlobalPoinException {

  private static final long serialVersionUID = 1L;

  public ClientNotFoundException(String clientId) {
    super(String.format("Client %s not found!", clientId));
  }
}
