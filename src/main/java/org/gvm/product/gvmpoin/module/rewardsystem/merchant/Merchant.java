package org.gvm.product.gvmpoin.module.rewardsystem.merchant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.PsJsonView;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
@Entity
@Table(name = "reward_system_merchant")
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Merchant {

  @Id
  @SequenceGenerator(
      name = "reward_system_merchant_seq",
      sequenceName = "reward_system_merchant_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "reward_system_merchant_seq")
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
  private String coverUrl = "";

  private Date createdTime;

  @JsonProperty("merchant_Id")
  private String merchantId;

}
