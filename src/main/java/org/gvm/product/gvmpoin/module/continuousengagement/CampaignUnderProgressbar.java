package org.gvm.product.gvmpoin.module.continuousengagement;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.gvm.product.gvmpoin.module.campaign.Campaign;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.Progressbar;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "campaign_under_progressbar")
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("serial")
@IdClass(CampaignUnderProgressbar.class)
public class CampaignUnderProgressbar implements Serializable {

  @Id
  @ManyToOne
  @JoinColumn(name = "progressbar_id")
  private Progressbar progressbar;

  @Id
  @ManyToOne
  @JoinColumn(name = "campaign_id")
  private Campaign campaign;

  private Date createdAt;

  public CampaignUnderProgressbar() {
  }

  public CampaignUnderProgressbar(Progressbar progressbar, Campaign campaign) {
    this.progressbar = progressbar;
    this.campaign = campaign;
  }

  @PrePersist
  void createdAt() {
    this.createdAt = new Date();
  }
}
