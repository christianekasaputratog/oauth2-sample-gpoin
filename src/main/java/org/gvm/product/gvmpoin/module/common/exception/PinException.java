package org.gvm.product.gvmpoin.module.common.exception;

/**
 * Created by marcelina.panggabean on 10/23/2017.
 */
public class PinException extends GlobalPoinException {

  private static final long serialVersionUID = -3747584745463645144L;

  public PinException() {
    super("Pin doesn't match");
  }

}