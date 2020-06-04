package org.gvm.product.gvmpoin.module.rewardsystem.supplier;

import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.user.LoginUser;
import org.gvm.product.gvmpoin.util.DateUtil;

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
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "reward_system_supplier")
@lombok.Data
public class Supplier {

  @Id
  @SequenceGenerator(
      name = "reward_system_supplier_seq",
      sequenceName = "reward_system_supplier_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "reward_system_supplier_seq")
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.Consumer.class, PsJsonView.RewardSystemRedeemedVoucher.class})
  private Long id;

  @NotNull
  @JsonView({PsJsonView.RewardSystemReward.class, PsJsonView.RewardSystemRewardTaken.class,
      PsJsonView.Consumer.class, PsJsonView.RewardSystemRedeemedVoucher.class})
  private String name;

  private Date createdTime;

  private Date updatedTime;

  @ManyToOne
  @JoinColumn(name = "supplier_cs_login_id")
  private LoginUser supplierCreator;

  private String supplierModifier;

  @PrePersist
  public void onCreate() {
    createdTime = DateUtil.getTimeNow();
  }

  @PreUpdate
  public void onUpdate() {
    updatedTime = DateUtil.getTimeNow();
  }
}
