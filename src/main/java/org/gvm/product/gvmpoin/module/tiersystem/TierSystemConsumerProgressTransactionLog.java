package org.gvm.product.gvmpoin.module.tiersystem;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.gvm.product.gvmpoin.util.DateUtil;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "tier_system_consumer_progress_transaction_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
@lombok.Data
public class TierSystemConsumerProgressTransactionLog {

  @Id
  @SequenceGenerator(
      name = "tier_system_consumer_progress_transaction_log_seq",
      sequenceName = "tier_system_consumer_progress_transaction_log_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "tier_system_consumer_progress_transaction_log_seq")
  private Long id;

  @JsonProperty("tier_system_consumer_progress_id")
  private Long tierSystemConsumerProgressId;

  @JsonProperty("client_id")
  private String clientId;

  private Integer credit;

  private Integer debit;

  private Integer balance;

  private String description;

  private String activity;

  @JsonProperty("activity_object")
  private String activityObject;

  @JsonProperty("activity_object_id")
  private Long activityObjectId;

  @JsonProperty("additional_data")
  private String additionalData;

  @JsonProperty("created_time")
  private Date createdTime;

  @PrePersist
  public void onCreate() {
    createdTime = DateUtil.getTimeNow();
  }
}
