package org.gvm.product.gvmpoin.module.trialbalance;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class TrialBalanceNotFoundException extends GlobalPoinException {

  private static final long serialVersionUID = -1054928456036370507L;

  public TrialBalanceNotFoundException() {
    super("This consumer has no balance record!");
  }
}
