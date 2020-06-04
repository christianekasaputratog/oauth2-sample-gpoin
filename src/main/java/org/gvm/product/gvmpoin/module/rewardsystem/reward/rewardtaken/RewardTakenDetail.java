package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken;

import org.gvm.product.gvmpoin.module.common.PsJsonView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "reward_system_reward_taken_detail")
@lombok.Data
public class RewardTakenDetail {

  @Id
  @SequenceGenerator(
      name = "reward_system_reward_taken_detail_seq",
      sequenceName = "reward_system_reward_taken_detail__id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "reward_system_reward_taken_detail_seq")
  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Long id;

  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  @JsonProperty("recipient_name")
  private String recipientName;

  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  @JsonProperty("phone_number")
  private String phoneNumber;

  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private String address;

  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  @JsonProperty("post_code")
  private String postCode;

  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private String city;

  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  @JsonProperty("additional_info")
  private String additionalInfo;

  @JsonProperty("facebook_account")
  private String facebookAccount;

  @JsonProperty("twitter_account")
  private String twitterAccount;

  @JsonProperty("instagram_account")
  private String instagramAccount;

  @JsonView({PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  @JsonProperty("social_media")
  @Transient
  private RewardSocialMedia rewardSocialMedia;
}