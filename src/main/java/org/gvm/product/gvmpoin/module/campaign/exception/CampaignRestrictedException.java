package org.gvm.product.gvmpoin.module.campaign.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class CampaignRestrictedException extends GlobalPoinException {

  private static final long serialVersionUID = 6446280577464811031L;

  public CampaignRestrictedException() {
    super(String.format("This campaign is restricted!"));
  }
}
