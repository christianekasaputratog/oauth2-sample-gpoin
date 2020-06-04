package org.gvm.product.gvmpoin.module.continuousengagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.continuousengagement.progressbar.Progressbar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "continous_engagement_goal")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Goal {

  @Id
  @GeneratedValue(generator = "goal_seq")
  @SequenceGenerator(name = "goal_seq", sequenceName = "goal_seq_id_seq",
      allocationSize = 1)
  @JsonView(PsJsonView.Progressbar.class)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "progressbar_id")
  @JsonView(PsJsonView.Progressbar.class)
  private Progressbar progressbar;

  @JsonView(PsJsonView.Progressbar.class)
  private Integer maximumCount;

  @JsonView(PsJsonView.Progressbar.class)
  private Integer rewardPoint;

  public Goal() {
  }

  public Goal(Progressbar progressbar, Integer target, Integer reward) {
    this.progressbar = progressbar;
    this.maximumCount = target;
    this.rewardPoint = reward;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Progressbar getProgressbar() {
    return progressbar;
  }

  public void setProgressbar(Progressbar progressbar) {
    this.progressbar = progressbar;
  }

  public Integer getMaximumCount() {
    return maximumCount;
  }

  public void setMaximumCount(Integer maximumCount) {
    this.maximumCount = maximumCount;
  }

  public Integer getRewardPoint() {
    return rewardPoint;
  }

  public void setRewardPoint(Integer rewardPoint) {
    this.rewardPoint = rewardPoint;
  }
}
