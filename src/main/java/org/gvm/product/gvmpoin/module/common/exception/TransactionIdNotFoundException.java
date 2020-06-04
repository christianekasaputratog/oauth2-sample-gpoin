package org.gvm.product.gvmpoin.module.common.exception;

public class TransactionIdNotFoundException extends GlobalPoinException {

  private static final long serialVersionUID = 4432877106527048547L;

  public TransactionIdNotFoundException(Long transactionId) {
    super(String.format("transaction Id %s not found!", transactionId));
  }
}
