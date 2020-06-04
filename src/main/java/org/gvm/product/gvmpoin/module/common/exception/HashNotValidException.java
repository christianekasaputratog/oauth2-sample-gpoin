package org.gvm.product.gvmpoin.module.common.exception;

public class HashNotValidException extends GlobalPoinException {

  private static final long serialVersionUID = -8137213564826540940L;

  public HashNotValidException(String hash) {
    super(String.format("%s not valid!", hash));
  }

}
