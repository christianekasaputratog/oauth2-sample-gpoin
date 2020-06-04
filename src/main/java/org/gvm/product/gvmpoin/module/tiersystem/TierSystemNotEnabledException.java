package org.gvm.product.gvmpoin.module.tiersystem;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class TierSystemNotEnabledException extends GlobalPoinException {

  public TierSystemNotEnabledException() {
    super("Feature Tier System is not enabled for the current client.");
  }
}
