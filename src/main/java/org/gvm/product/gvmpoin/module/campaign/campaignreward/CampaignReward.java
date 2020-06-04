package org.gvm.product.gvmpoin.module.campaign.campaignreward;

import org.gvm.product.gvmpoin.module.campaign.Campaign;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.rewardsystem.reward.Reward;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "campaign_reward")
@lombok.Data
public class CampaignReward implements Serializable {

  @Id
  @GeneratedValue(generator = "campaign_reward_seq")
  @SequenceGenerator(
      name = "campaign_reward_seq",
      sequenceName = "campaign_reward_seq_id_seq",
      allocationSize = 1)
  private Long id;

  @JsonView(PsJsonView.RewardSystem.class)
  @JsonProperty("reward_id")
  @OneToOne
  @JoinColumn(name = "reward_id")
  private Reward rewardId;

  @JsonView(PsJsonView.Campaign.class)
  @JsonProperty("campaign_id")
  @ManyToOne
  @JoinColumn(name = "campaign_id")
  private Campaign campaignId;

  @JsonView(PsJsonView.User.class)
  private Date createdTime;

  @JsonView(PsJsonView.User.class)
  private Date updatedTime;
}