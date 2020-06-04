package org.gvm.product.gvmpoin.module.rewardsystem.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class RewardAlreadyExistException extends GlobalPoinException {

  public RewardAlreadyExistException(Long rewardId) {
    super(String.format("Reward %s already exist!", rewardId));
  }
}
