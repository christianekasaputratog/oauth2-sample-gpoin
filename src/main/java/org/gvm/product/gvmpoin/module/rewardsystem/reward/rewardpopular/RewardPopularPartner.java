package org.gvm.product.gvmpoin.module.rewardsystem.reward.rewardpopular;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "reward_system_reward_popular_partner")
@Data
public class RewardPopularPartner {

  @Id
  @SequenceGenerator(
      name = "reward_system_reward_popular_partner_seq",
      sequenceName = "reward_system_reward_popular_partner_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "reward_system_reward_popular_partner_seq")
  private Long id;

  @Column(name = "reward_popular_id")
  private Long rewardPopularId;

  @Column(name = "partner_id")
  private Long partnerId;
}
