package org.gvm.product.gvmpoin.module.rewardsystem.promotion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.user.LoginUser;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 * Created by bobbi.sinaga on 7/17/2017.
 */
@Entity
@Table(name = "reward_system_promotion")
@lombok.Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Promotion {

  @Id
  @SequenceGenerator(
      name = "reward_system_promotion_seq",
      sequenceName = "reward_system_promotion_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "reward_system_promotion_seq")
  private Long id;

  @NotNull
  @JsonView(PsJsonView.RewardSystemPromotion.class)
  private String name;

  @NotNull
  @JsonProperty("banner_url")
  @JsonView(PsJsonView.RewardSystemPromotion.class)
  private String bannerUrl;

  @NotNull
  @JsonProperty("target_url")
  @JsonView(PsJsonView.RewardSystemPromotion.class)
  private String targetUrl;

  @NotNull
  @JsonProperty("start_date")
  @Temporal(TemporalType.DATE)
  private Date startDate;

  @JsonProperty("end_date")
  @Temporal(TemporalType.DATE)
  private Date endDate;

  @JsonProperty("updated_time")
  @JsonView(PsJsonView.RewardSystemPromotion.class)
  private Date updatedTime;

  @JsonProperty("removed_time")
  @JsonView(PsJsonView.RewardSystemPromotion.class)
  private Date removedTime;

  @JsonProperty("created_time")
  @JsonView(PsJsonView.RewardSystemPromotion.class)
  private Date createdTime;

  @JsonView(PsJsonView.RewardSystemPromotion.class)
  private Integer status;

  @ManyToOne
  @JoinColumn(name = "promotion_cs_login_id")
  private LoginUser promotionCreator;

}
