package org.gvm.product.gvmpoin.module.rewardsystem.reward;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.rewardsystem.merchant.Merchant;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardcategory.RewardCategory;
import org.gvm.product.gvmpoin.module.rewardsystem.supplier.Supplier;
import org.gvm.product.gvmpoin.util.DateUtil;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
@Entity
@Table(name = "reward_system_reward")
@lombok.Data
public class Reward {

  @Version
  private long version;

  @Id
  @SequenceGenerator(
      name = "reward_system_reward_seq",
      sequenceName = "reward_system_reward_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "reward_system_reward_seq")
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

  @JsonProperty("custom_cover_url")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private String customCoverUrl;

  @Column(length = 1500)
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private String tnc;

  @JsonProperty("remain_stock")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Integer remainStock = 0;

  @JsonProperty("total_stock")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.RewardSystemRedeemedVoucher.class})
  private Integer totalStock = 0;

  @JsonProperty("point_cost")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Integer pointCost = 0;

  @JsonProperty("point_value")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Integer pointValue = 0;

  @ManyToOne
  @JoinColumn(name = "reward_system_category_id")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private RewardCategory category;

  @ManyToOne
  @JoinColumn(name = "reward_system_merchant_id")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Merchant merchant;

  @NotNull
  @JsonProperty("created_time")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Date createdTime;

  @JsonProperty("updated_time")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Date updatedTime;

  @NotNull
  @Enumerated(EnumType.STRING)
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private RewardType type;

  @JsonProperty("status")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Integer status;

  @JsonProperty("event_date")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Date eventDate;

  @JsonProperty("expired_date")
  private Date expiredDate;

  @JsonProperty("published_date")
  private Date publishedDate;

  @JsonProperty("ordinal_time")
  private Date ordinalTime;

  @JsonProperty("program_id")
  private String programId;

  @JsonProperty("brand")
  private String brand;

  @JsonProperty("value")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.RewardSystemRedeemedVoucher.class})
  private Integer value;

  @ManyToOne
  @JoinColumn(name = "reward_system_supplier_id")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Supplier supplier;

  @JsonProperty("voucher_taken_expired_in_days")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Integer takenExpiredDate = 0;

  @JsonProperty("tada_claim_type")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private String tadaClaimType;

  @JsonProperty("voucher_expiry_on_date")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Date expiryOnDate;

  @JsonProperty("item_type")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private Integer itemType;

  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardCategory.class,
      PsJsonView.RewardSystemRewardTaken.class, PsJsonView.Consumer.class,
      PsJsonView.RewardSystemRedeemedVoucher.class})
  private String description;

  @JsonView(PsJsonView.RewardSystemReward.class)
  @JsonProperty("is_wish_list")
  @Transient
  private Integer isWishList = 0;

  @JsonProperty("total_wish_list")
  @Transient
  @JsonView(PsJsonView.RewardSystemReward.class)
  private Integer totalWishList;

  @JsonProperty("is_campaign_reward")
  @JsonView(PsJsonView.RewardSystemReward.class)
  private Integer isCampaignReward;

  @PrePersist
  public void onCreate() {
    createdTime = DateUtil.getTimeNow();
  }

  @PreUpdate
  public void onUpdate() {
    updatedTime = DateUtil.getTimeNow();
  }
}