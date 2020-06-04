package org.gvm.product.gvmpoin.module.rewardsystem.promotion;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.rewardsystem.partner.Partner;
import org.gvm.product.gvmpoin.util.DateUtil;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "reward_system_promotion_partner")
@Entity
@Data
public class PromotionPartner {

  @Id
  @GeneratedValue(generator = "reward_system_promotion_partner_seq")
  @SequenceGenerator(
      name = "reward_system_promotion_partner_seq",
      sequenceName = "reward_system_promotion_partner_seq_id_seq",
      allocationSize = 1)
  private Long id;

  @JsonProperty("promotion")
  @ManyToOne
  @JoinColumn(name = "promotion_id")
  private Promotion promotion;

  @JsonView({PsJsonView.RewardSystemPromotion.class, PsJsonView.RewardSystemReward.class})
  @JsonProperty("partner")
  @ManyToOne
  @JoinColumn(name = "partner_id")
  private Partner partner;

  @JsonView({PsJsonView.RewardSystemPromotion.class, PsJsonView.RewardSystemReward.class})
  @JsonProperty("created_date")
  private Date createdDate;

  @PrePersist
  public void onCreate() {
    createdDate = DateUtil.getTimeNow();
  }
}