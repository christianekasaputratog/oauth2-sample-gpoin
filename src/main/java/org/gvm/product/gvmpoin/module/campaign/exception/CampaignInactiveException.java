package org.gvm.product.gvmpoin.module.campaign.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

@SuppressWarnings("serial")
public class CampaignInactiveException extends GlobalPoinException {

  public CampaignInactiveException(String message) {
    super(message);
  }
}
