package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardcategory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
@Entity
@Table(name = "reward_system_reward_category")
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "cover_url", "rewards"})
public class RewardCategory {

  @Id
  @SequenceGenerator(
      name = "reward_system_reward_category_seq",
      sequenceName = "reward_system_reward_category_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "reward_system_reward_category_seq")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Long id;

  @NotNull
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private String name;

  @JsonProperty("cover_url")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private String coverUrl;

  @JsonView(PsJsonView.RewardSystemRewardCategory.class)
  @Transient
  private List<Reward> rewards;
}
