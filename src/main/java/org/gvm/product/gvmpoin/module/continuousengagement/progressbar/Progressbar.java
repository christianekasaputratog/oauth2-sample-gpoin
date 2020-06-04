package org.gvm.product.gvmpoin.module.continuousengagement.progressbar;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.continuousengagement.CampaignUnderProgressbar;
import org.gvm.product.gvmpoin.module.continuousengagement.Goal;
import org.gvm.product.gvmpoin.module.continuousengagement.MyProgress;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "continous_engagement_progressbar")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Progressbar {

  @Id
  @GeneratedValue(generator = "progressbar_seq")
  @SequenceGenerator(name = "progressbar_seq", sequenceName = "progressbar_seq_id_seq",
      allocationSize = 1)
  @JsonView(PsJsonView.Progressbar.class)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "client_id")
  @JsonView(PsJsonView.Progressbar.class)
  private Client client;

  @JsonView(PsJsonView.Progressbar.class)
  private Date createdTime;
  private Date latestUpdatedTime;
  private Boolean isLimitOneCountPerDay;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "progressbar", cascade = CascadeType.ALL)
  @OrderBy("id ASC")
  @JsonView(PsJsonView.Progressbar.class)
  private List<Goal> goals;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "progressbar", cascade = CascadeType.ALL)
  @OrderBy("id DESC")
  private List<MyProgress> myProgress;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "progressbar", cascade = CascadeType.ALL)
  private List<CampaignUnderProgressbar> appliedToCampaigns;

  @Column(name = "active", columnDefinition = "Boolean default FALSE")
  private Boolean active;

  @Transient
  private String strListofCampaigns;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public Date getLatestUpdatedTime() {
    return latestUpdatedTime;
  }

  public void setLatestUpdatedTime(Date latestUpdatedTime) {
    this.latestUpdatedTime = latestUpdatedTime;
  }

  public Boolean getIsLimitOneCountPerDay() {
    return isLimitOneCountPerDay;
  }

  public void setIsLimitOneCountPerDay(Boolean isLimitOneCountPerDay) {
    this.isLimitOneCountPerDay = isLimitOneCountPerDay;
  }

  public List<Goal> getGoals() {
    return goals;
  }

  public void setGoals(List<Goal> goals) {
    this.goals = goals;
  }

  public List<MyProgress> getMyProgress() {
    return myProgress;
  }

  public void setMyProgress(List<MyProgress> myProgress) {
    this.myProgress = myProgress;
  }

  public List<CampaignUnderProgressbar> getAppliedToCampaigns() {
    return appliedToCampaigns;
  }

  public void setAppliedToCampaigns(List<CampaignUnderProgressbar> appliedToCampaigns) {
    this.appliedToCampaigns = appliedToCampaigns;
  }

  public Date getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }

  public Boolean getStatusActive() {
    return active;
  }

  public void setStatusActive(Boolean status) {
    this.active = status;
  }

  public String getStrListofCampaigns() {
    return strListofCampaigns;
  }

  public void setStrListofCampaigns(String strListofCampaigns) {
    this.strListofCampaigns = strListofCampaigns;
  }

  @PrePersist
  void createdAt() {
    this.createdTime = new Date();
  }

  @PreUpdate
  void updatedAt() {
    this.latestUpdatedTime = new Date();
  }

  @Override
  public String toString() {
    return "Progressbar [id=" + id + ", client=" + client.getClientId() + ", createdTime="
        + createdTime + ", latestUpdatedTime=" + latestUpdatedTime + ", isLimitOneCountPerDay="
        + isLimitOneCountPerDay + "]";
  }
}
