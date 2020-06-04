package org.gvm.product.gvmpoin.module.tiersystem;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.util.DateUtil;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tier_system_master")
@JsonInclude(JsonInclude.Include.NON_NULL)
@lombok.Data
public class TierSystemMaster {

  @Id
  @SequenceGenerator(
      name = "tier_system_master_seq",
      sequenceName = "tier_system_master_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "tier_system_master_seq")
  private Long id;

  @OneToOne
  @JoinColumn(name = "client_id")
  private Client client;

  @NotNull
  @JsonProperty("start_date")
  private Date startDate;

  @NotNull
  @JsonProperty("reset_interval")
  private Integer resetInterval;

  @NotNull
  @JsonProperty("created_time")
  private Date createdTime;

  //    @NotNull
  @JsonProperty("updated_time")
  private Date updatedTime;

  @PrePersist
  public void onCreate() {
    createdTime = DateUtil.getTimeNow();
  }

  @PreUpdate
  public void onUpdate() {
    updatedTime = DateUtil.getTimeNow();
  }
}
