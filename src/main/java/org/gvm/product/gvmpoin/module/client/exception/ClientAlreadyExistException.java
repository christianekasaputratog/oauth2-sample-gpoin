package org.gvm.product.gvmpoin.module.client.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class ClientAlreadyExistException extends GlobalPoinException {

  private static final long serialVersionUID = 5057722957205274663L;

  public ClientAlreadyExistException(String clientId) {
    super(String.format("Client %s already exists.", clientId));
  }
}
