package org.gvm.product.gvmpoin.module.rewardsystem.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class RewardNotFoundException extends GlobalPoinException {

  public RewardNotFoundException(Long rewardId) {
    super(String.format("Reward %s Not Found !", rewardId));
  }

}
