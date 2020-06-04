package org.gvm.product.gvmpoin.module.tiersystem;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.util.DateUtil;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tier_system_consumer_progress")
@JsonInclude(JsonInclude.Include.NON_NULL)
@lombok.Data
public class TierSystemConsumerProgress {
  @Id
  @SequenceGenerator(
          name = "tier_system_consumer_progress_seq",
          sequenceName = "tier_system_consumer_progress_id_seq",
          allocationSize = 1)
  @GeneratedValue(
          strategy = GenerationType.AUTO,
          generator = "tier_system_consumer_progress_seq")
  private Long id;

  @NotNull
  @Column(unique = true)
  private Long tierSystemMasterId;

  @OneToOne
  @JoinColumn(name = "consumer_id")
  private Consumer consumer;

  @ManyToOne
  @JoinColumn(name = "current_tier_system_level_id")
  @JsonProperty("current_level")
  @JsonView(PsJsonView.TierSystemConsumerProgress.class)
  private TierSystemLevel currentLevel;

  private Integer openingBalance;

  @JsonProperty("level_point")
  @JsonView(PsJsonView.TierSystemConsumerProgress.class)
  private Integer closingBalance;

  private Integer totalDebitMutation;

  private Integer totalCreditMutation;

  private Date createdTime;

  private Date updatedTime;

  @Transient
  @JsonView(PsJsonView.TierSystemConsumerProgress.class)
  private List<TierSystemLevel> levels;

  @PrePersist
  public void onCreate() {
    createdTime = DateUtil.getTimeNow();
  }

  @PreUpdate
  public void onUpdate() {
    updatedTime = DateUtil.getTimeNow();
  }
}
