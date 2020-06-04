package org.gvm.product.gvmpoin.module.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.campaign.leaderboard.Leaderboard;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntry;

import lombok.Data;

/**
 * Created by sofian.hadianto on 2/27/2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class RollbackTransaction {

  @JsonProperty("journalentry")
  @JsonView(PsJsonView.RollbackSummary.class)
  private JournalEntry journalEntryAfterRollback;

  @JsonProperty("leaderboard")
  @JsonView(PsJsonView.RollbackSummary.class)
  private Leaderboard leaderboardAfterRollback;

  public RollbackTransaction(JournalEntry journalEntryAfterRollback,
      Leaderboard leaderboardAfterRollback) {
    this.journalEntryAfterRollback = journalEntryAfterRollback;
    this.leaderboardAfterRollback = leaderboardAfterRollback;
  }

}
