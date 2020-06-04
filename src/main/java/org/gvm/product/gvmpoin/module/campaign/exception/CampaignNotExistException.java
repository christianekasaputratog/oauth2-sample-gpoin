package org.gvm.product.gvmpoin.module.campaign.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

@SuppressWarnings("serial")
public class CampaignNotExistException extends GlobalPoinException {

  public CampaignNotExistException() {
    super("Sorry! campaign unavailable");
  }
}
