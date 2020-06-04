package org.gvm.product.gvmpoin.module.journalentry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalance;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@JsonPropertyOrder({"id", "client_id", "debit", "credit", "description", "transaction_time"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class JournalEntry {

  @Id
  @GeneratedValue(generator = "journalentrynew_seq")
  @SequenceGenerator(
      name = "journalentrynew_seq",
      sequenceName = "journalentrynew_seq_id_seq",
      allocationSize = 1)
  @JsonProperty("transaction_id")
  @JsonView(value = {PsJsonView.Journal.class, PsJsonView.RollbackSummary.class})
  private Long id;

  @JsonProperty("client_id")
  private String clientId;

  @JsonView(value = {PsJsonView.Journal.class, PsJsonView.RollbackSummary.class})
  private Integer debit;

  @JsonView(value = {PsJsonView.Journal.class, PsJsonView.RollbackSummary.class})
  private Integer credit;

  @JsonView(value = {PsJsonView.Journal.class, PsJsonView.RollbackSummary.class})
  private Integer balance;

  @JsonView(value = {PsJsonView.Journal.class, PsJsonView.RollbackSummary.class})
  private String description;

  @JsonProperty("transaction_time")
  @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP")
  @Temporal(TemporalType.TIMESTAMP)
  @JsonView(value = {PsJsonView.Journal.class, PsJsonView.RollbackSummary.class})
  private Date transactionTime;

  @ManyToOne
  @JoinColumn(name = "trial_balance_id")
  @JsonProperty("trial_balance")
  @JsonView(PsJsonView.JournalWithTrialBalance.class)
  private TrialBalance trialBalance;

  @ManyToOne
  @JoinColumn(name = "consumer_id")
  private Consumer consumer;

  private String activity;

  private String activityObject;

  private Long objectId;

  private String additionalData;

  private String clientTransactionId;

  @Transient
  @JsonView(PsJsonView.Journal.class)
  private Client client;

  @Transient
  @JsonView(PsJsonView.Journal.class)
  @JsonProperty("transaction_activity")
  private String transactionActivity;

  @Column(name = "is_fraud_transaction",
      columnDefinition = "boolean DEFAULT false",
      nullable = false,
      insertable = false)
  private boolean isFraudTransaction;

  @PrePersist
  void onCreate() {
    this.transactionTime = new Date();
    this.isFraudTransaction = false;
  }
}
