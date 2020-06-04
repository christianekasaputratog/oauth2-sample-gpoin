package org.gvm.product.gvmpoin.module.rewardsystem.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class RewardTypeNotFoundException extends GlobalPoinException {

  public RewardTypeNotFoundException(String rewardType) {
    super(String.format("Reward type %s undefined !", rewardType));
  }

}
