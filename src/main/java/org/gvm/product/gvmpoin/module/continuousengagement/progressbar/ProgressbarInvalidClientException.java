package org.gvm.product.gvmpoin.module.continuousengagement.progressbar;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

@SuppressWarnings("serial")
public class ProgressbarInvalidClientException extends GlobalPoinException {

  public ProgressbarInvalidClientException() {
    super(String.format("Invalid client"));
  }
}
