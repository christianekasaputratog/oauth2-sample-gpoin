package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
@Entity
@Table(name = "reward_system_reward_taken")
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RewardTaken {

  @Version
  private long version;

  @Id
  @SequenceGenerator(
      name = "reward_system_reward_taken_seq",
      sequenceName = "reward_system_reward_taken_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "reward_system_reward_taken_seq")
  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Long id;

  @OneToOne
  @JoinColumn(name = "reward_system_reward_id")
  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Reward reward;

  @OneToOne
  @JoinColumn(name = "consumer_id")
  private Consumer consumer;

  @NotNull
  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Integer status;

  @NotNull
  @JsonProperty("reward_code")
  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private String code;

  @JsonProperty("serial_number")
  @JsonView(PsJsonView.RewardSystemRedeemedVoucher.class)
  private String serialNumber;

  @JsonProperty("approval_code")
  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private String approvalCode;

  @JsonProperty("expired_date")
  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Date expiredDate;

  @JsonProperty("redeemed_date")
  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Date redeemedDate;

  private Date ordinalTime;

  private Date takenDate;

  @JsonProperty("reward_type")
  @JsonView({PsJsonView.RewardSystemRewardTaken.class, PsJsonView.RewardSystemRedeemedVoucher.class,
      PsJsonView.RewardSystemReward.class})
  private String rewardType;

  @JsonProperty("tada_claim_type")
  @JsonView({PsJsonView.RewardSystemRewardTaken.class, PsJsonView.RewardSystemRedeemedVoucher.class,
      PsJsonView.RewardSystemReward.class})
  private String tadaClaimType;

  private String url;

  @JsonProperty("transaction_status")
  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private String transactionStatus;

  @Transient
  @JsonProperty("mobile_number")
  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private String mobileNumber;

  @OneToOne(cascade = CascadeType.ALL)
  @JsonProperty("reward_taken_detail")
  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private RewardTakenDetail rewardTakenDetail;
}
