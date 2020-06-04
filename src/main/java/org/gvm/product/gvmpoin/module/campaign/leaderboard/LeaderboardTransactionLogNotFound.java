package org.gvm.product.gvmpoin.module.campaign.leaderboard;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

/**
 * Created by sofian-hadianto on 2/10/17.
 */
@SuppressWarnings("serial")
public class LeaderboardTransactionLogNotFound extends GlobalPoinException {

  public LeaderboardTransactionLogNotFound(Long id) {
    super(String.format("Transaction Log Id %d not found!", id));
  }
}
