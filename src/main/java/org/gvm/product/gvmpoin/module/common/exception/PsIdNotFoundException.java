package org.gvm.product.gvmpoin.module.common.exception;

public class PsIdNotFoundException extends GlobalPoinException {

  private static final long serialVersionUID = -3747584745463645144L;

  public PsIdNotFoundException(String psId) {
    super(String.format("Ps Id %s not found/invalid!", psId));
  }
}
