package org.gvm.product.gvmpoin.module.campaign;

import org.gvm.product.gvmpoin.module.common.PsJsonView;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

@Entity
@Data
public class CampaignChild {

  @Id
  @GeneratedValue(generator = "campaign_child_seq")
  @SequenceGenerator(
      name = "campaign_child_seq",
      sequenceName = "campaign_child_seq_id_seq",
      allocationSize = 1)
  private Long id;

  @JsonView(PsJsonView.Campaign.class)
  @JsonProperty("campaign_parent_id")
  @ManyToOne
  @JoinColumn(name = "campaign_parent_id")
  private Campaign campaignParent;

  @JsonView(PsJsonView.Campaign.class)
  @JsonProperty("campaign_child_id")
  @OneToOne
  @JoinColumn(name = "campaign_child_id")
  private Campaign campaignChild;
}