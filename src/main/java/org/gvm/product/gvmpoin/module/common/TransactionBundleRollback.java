package org.gvm.product.gvmpoin.module.common;

import lombok.Data;

/**
 * Created by sofian-hadianto on 2/9/17.
 */
@Data
public class TransactionBundleRollback {

  private String transactionId;

  private String category; // balance (reguler_poin) or leaderboard (campaign_poin)

  private String campaignUniqueCode; // khusus utk leaderboard

  private String reason;

  @Override
  public String toString() {
    return "TransactionBundleRollback{"
        + "transactionId=" + transactionId
        + ", category='" + category + '\''
        + ", campaign_unique_code='" + campaignUniqueCode + '\''
        + ", reason='" + reason + '\''
        + '}';
  }
}
