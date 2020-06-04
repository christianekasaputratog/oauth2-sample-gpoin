package org.gvm.product.gvmpoin.module.campaign.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

@SuppressWarnings("serial")
public class CampaignClosedException extends GlobalPoinException {

  public CampaignClosedException(String message) {
    super(message);
  }
}
