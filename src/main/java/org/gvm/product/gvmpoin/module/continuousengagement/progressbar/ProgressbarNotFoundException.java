package org.gvm.product.gvmpoin.module.continuousengagement.progressbar;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

@SuppressWarnings("serial")
public class ProgressbarNotFoundException extends GlobalPoinException {

  public ProgressbarNotFoundException() {
    super(String.format("Progressbar is not found!"));
  }
}
