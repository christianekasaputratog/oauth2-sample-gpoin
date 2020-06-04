package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardtaken;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.gvm.product.gvmpoin.module.common.PsJsonView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RewardSocialMedia implements Serializable {

  @JsonView({PsJsonView.RewardSystemRewardTaken.class, PsJsonView.RewardSystemRedeemedVoucher.class})
  @JsonProperty("facebook_account")
  private String facebookAccount;

  @JsonView({PsJsonView.RewardSystemRewardTaken.class, PsJsonView.RewardSystemRedeemedVoucher.class})
  @JsonProperty("twitter_account")
  private String twitterAccount;

  @JsonView({PsJsonView.RewardSystemRewardTaken.class, PsJsonView.RewardSystemRedeemedVoucher.class})
  @JsonProperty("instagram_account")
  private String instagramAccount;
}