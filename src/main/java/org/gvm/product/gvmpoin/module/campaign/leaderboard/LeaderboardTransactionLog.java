package org.gvm.product.gvmpoin.module.campaign.leaderboard;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.util.DateUtil;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "campaign_leaderboard_transaction_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaderboardTransactionLog {

  @Id
  @GeneratedValue(generator = "leaderboard_translog_seq")
  @SequenceGenerator(name = "leaderboard_translog_seq",
      sequenceName = "leaderboard_translog_id_seq", allocationSize = 1)
  private Long id;

  private Integer balance;

  @JsonView(PsJsonView.Leaderboard.class)
  private Integer credit;

  @JsonView(PsJsonView.Leaderboard.class)
  private Integer debit;

  private String description;

  @ManyToOne
  private Leaderboard leaderboard;

  private Date transactionTime;

  @JsonView(PsJsonView.Leaderboard.class)
  private String activity;

  @JsonView(PsJsonView.Leaderboard.class)
  @JsonProperty("activity_object")
  private String activityObject;

  private Long objectId;

  private String clientTransactionId;

  private String additionalData;

  @Column(name = "is_fraud_transaction",
      columnDefinition = "boolean DEFAULT false",
      nullable = false,
      insertable = false)
  private boolean isFraudTransaction;

  @PrePersist
  public void onCreate() {
    this.transactionTime = DateUtil.getTimeNow();
    this.isFraudTransaction = false;
  }
}