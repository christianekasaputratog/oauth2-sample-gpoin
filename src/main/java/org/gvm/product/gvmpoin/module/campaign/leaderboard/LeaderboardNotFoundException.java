package org.gvm.product.gvmpoin.module.campaign.leaderboard;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

/**
 * Created by sofian-hadianto on 3/16/17.
 */
@SuppressWarnings("serial")
public class LeaderboardNotFoundException extends GlobalPoinException {

  public LeaderboardNotFoundException(Long id) {
    super(String.format("Leaderboard id %d not found", id));
  }
}
