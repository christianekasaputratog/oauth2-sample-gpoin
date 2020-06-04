package org.gvm.product.gvmpoin.module.campaign.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

@SuppressWarnings("serial")
public class CampaignExpiredException extends GlobalPoinException {

  public CampaignExpiredException(String message) {
    super(message);
  }
}
