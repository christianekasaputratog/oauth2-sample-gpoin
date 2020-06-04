package org.gvm.product.gvmpoin.module.trialbalance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.journalentry.JournalEntry;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@JsonPropertyOrder({"id", "opening_balance", "total_debits", "total_credits", "closing_balance",
    "last_updated_time"})
public class TrialBalance {

  @Id
  @GeneratedValue(generator = "trialbalancenew_seq")
  @SequenceGenerator(name = "trialbalancenew_seq", sequenceName = "trialbalancenew_seq_id_seq",
      allocationSize = 1)
  @JsonView(PsJsonView.TrialBalance.class)
  private Long id;

  @OneToOne(mappedBy = "trialBalance")
  private Consumer owner;

  @JsonProperty("opening_balance")
  @JsonView(value = {
      PsJsonView.TrialBalance.class,
      PsJsonView.JournalWithTrialBalance.class,
      PsJsonView.ConsumerWithBalance.class})
  private Integer openingBalance = 0;

  @JsonProperty("total_debits")
  @JsonView(value = {
      PsJsonView.TrialBalance.class,
      PsJsonView.JournalWithTrialBalance.class,
      PsJsonView.ConsumerWithBalance.class})
  private Integer totalDebits = 0;

  @JsonProperty("total_credits")
  @JsonView(value = {
      PsJsonView.TrialBalance.class,
      PsJsonView.JournalWithTrialBalance.class,
      PsJsonView.ConsumerWithBalance.class})
  private Integer totalCredits = 0;

  @JsonProperty("closing_balance")
  @JsonView(value = {
      PsJsonView.TrialBalance.class,
      PsJsonView.JournalWithTrialBalance.class,
      PsJsonView.ConsumerWithBalance.class})
  private Integer closingBalance = 0;

  @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP")
  @Temporal(TemporalType.TIMESTAMP)
  @JsonProperty("last_updated_time")
  @JsonView(PsJsonView.TrialBalance.class)
  private Date lastUpdatedTime;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "trialBalance", cascade = CascadeType.ALL)
  private Set<JournalEntry> journalEntries;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Consumer getOwner() {
    return owner;
  }

  public void setOwner(Consumer owner) {
    this.owner = owner;
  }

  public Integer getOpeningBalance() {
    return openingBalance;
  }

  public void setOpeningBalance(Integer openingBalance) {
    this.openingBalance = openingBalance;
  }

  public Integer getTotalDebits() {
    return totalDebits;
  }

  public void setTotalDebits(Integer totalDebits) {
    this.totalDebits = totalDebits;
  }

  public Integer getTotalCredits() {
    return totalCredits;
  }

  public void setTotalCredits(Integer totalCredits) {
    this.totalCredits = totalCredits;
  }

  public Integer getClosingBalance() {
    return closingBalance;
  }

  public void setClosingBalance(Integer closingBalance) {
    this.closingBalance = closingBalance;
  }

  public Date getLastUpdatedTime() {
    return lastUpdatedTime;
  }

  public void setLastUpdatedTime(Date lastUpdatedTime) {
    this.lastUpdatedTime = lastUpdatedTime;
  }

  public Set<JournalEntry> getJournalEntries() {
    return journalEntries;
  }

  public void setJournalEntries(
      Set<JournalEntry> journalEntries) {
    this.journalEntries = journalEntries;
  }

  @PrePersist
  void createdAt() {
    this.lastUpdatedTime = new Date();
  }

  @PreUpdate
  void updatedAt() {
    this.lastUpdatedTime = new Date();
  }

  @Override
  public String toString() {
    return "TrialBalance [id=" + id + ", owner=" + owner + ", openingBalance=" + openingBalance
        + ", totalDebits=" + totalDebits + ", totalCredits=" + totalCredits + ", closingBalance="
        + closingBalance + "]";
  }
}
