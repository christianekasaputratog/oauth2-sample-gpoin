package org.gvm.product.gvmpoin.module.continuousengagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.consumer.Consumer;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.Progressbar;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
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

@Data
@Entity
@Table(name = "continous_engagement_myprogress")
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("serial")
public class MyProgress implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "myprogress_seq")
  @SequenceGenerator(name = "myprogress_seq", sequenceName = "myprogress_seq_id_seq",
      allocationSize = 1)
  @JsonView(PsJsonView.MyProgress.class)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "progressbar_id")
  @JsonProperty("progressbar")
  private Progressbar progressbar;

  @ManyToOne
  @JoinColumn(name = "consumer_id")
  @JsonView(PsJsonView.MyProgress.class)
  @JsonProperty("consumer")
  private Consumer consumer;

  private Integer openingBalance;
  private Integer totalDebitMutation;
  private Integer totalCreditMutation;

  @JsonView(PsJsonView.MyProgress.class)
  @JsonProperty("count")
  private Integer closingBalance;

  private Date createdTime;

  @JsonView(PsJsonView.MyProgress.class)
  @JsonProperty("latest_updated_time")
  private Date latestUpdatedTime;

  @JsonView(PsJsonView.MyProgress.class)
  @JsonProperty("achieved_goal")
  private Integer currentGoalAchieved;

  @Transient
  @JsonView(PsJsonView.MyProgress.class)
  @JsonProperty("list_of_goals")
  private String strListOfGoals;

  @Transient
  private String campaignUniqueCode;

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
    return "MyProgress [id=" + id + ", progressbar=" + progressbar.getId() + ", consumer="
        + consumer.getPsId() + ", count=" + closingBalance + ", createdTime="
        + createdTime + ", latestUpdatedTime=" + latestUpdatedTime + ", currentGoalAchieved="
        + currentGoalAchieved + "]";
  }
}
