package org.gvm.product.gvmpoin.module.campaign.leaderboard;

import org.gvm.product.gvmpoin.module.campaign.Campaign;
import org.gvm.product.gvmpoin.module.common.PsJsonView;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/*
 * 
 * CREATE INDEX leaderboad_index
 * ON leaderboard (closing_balance, campaignUniqueCode);
 * */
@Entity
@Table(name = "campaign_leaderboard")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Leaderboard {

  @Id
  @GeneratedValue(generator = "leaderboard_seq")
  @SequenceGenerator(name = "leaderboard_seq", sequenceName = "leaderboard_id_seq",
      allocationSize = 1)
  @JsonView(value = {PsJsonView.Leaderboard.class, PsJsonView.RollbackSummary.class})
  private Long id;

  @JsonProperty("campaign_id")
  @ManyToOne
  private Campaign campaign;

  @JsonProperty("ps_id")
  @JsonView(value = {PsJsonView.Leaderboard.class, PsJsonView.RollbackSummary.class,
      PsJsonView.CampaignLeaderBoard.class})
  private String psId;

  private Integer openingBalance;

  private Integer totalDebitMutation;

  private Integer totalCreditMutation;

  @JsonProperty("total_score")
  @JsonView(value = {PsJsonView.Leaderboard.class, PsJsonView.RollbackSummary.class,
      PsJsonView.CampaignLeaderBoard.class})
  private Integer closingBalance;

  private Date createdTime;

  @JsonProperty("last_updated_time")
  @JsonView(value = {PsJsonView.Leaderboard.class, PsJsonView.RollbackSummary.class,
      PsJsonView.CampaignLeaderBoard.class})
  private Date lastUpdatedTime;

  @JsonView(value = {PsJsonView.Leaderboard.class, PsJsonView.RollbackSummary.class,
      PsJsonView.CampaignLeaderBoard.class})
  private Long rank;

  @JsonProperty("count_of_participants")
  @JsonView(PsJsonView.Leaderboard.class)
  @Transient
  private Long countOfParticipants;

  @JsonProperty("leaderboard_transaction_log")
  @JsonView(PsJsonView.Leaderboard.class)
  @Transient
  private LeaderboardTransactionLog leaderboardTransactionLog;

  @PrePersist
  public void onCreate() {
    this.createdTime = new Date();
    this.lastUpdatedTime = new Date();
  }

  @PreUpdate
  public void onUpdate() {
    this.lastUpdatedTime = new Date();
  }
}