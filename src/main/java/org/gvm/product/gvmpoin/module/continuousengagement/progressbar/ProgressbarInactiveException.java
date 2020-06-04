package org.gvm.product.gvmpoin.module.continuousengagement.progressbar;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

@SuppressWarnings("serial")
public class ProgressbarInactiveException extends GlobalPoinException {

  public ProgressbarInactiveException() {
    super(String.format("Progressbar inactive."));
  }
}
