package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;
import org.gvm.product.gvmpoin.util.DateUtil;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "reward_system_reward_popular")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RewardPopular {

  @Id
  @SequenceGenerator(
      name = "reward_system_reward_seq",
      sequenceName = "reward_system_reward_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "reward_system_reward_seq")
  private Long id;

  @Column(name = "ordinal")
  private Integer ordinal;

  @OneToOne
  @JoinColumn(name = "reward_system_reward_id")
  private Reward reward;

  @Column(name = "created_time")
  private Date createdTime;

  @NotNull
  @Column(name = "is_active")
  @Enumerated(value = EnumType.ORDINAL)
  private RewardPopularStatus isActive;

  /**
   * Setup Value for Ordinal, isActive and Created Time .
   */
  @PrePersist
  public void onCreate() {
    ordinal = Integer.MAX_VALUE;
    isActive = RewardPopularStatus.ACTIVE;
    createdTime = DateUtil.getTimeNow();
  }

}
