package org.gvm.product.gvmpoin.module.tiersystem;

import org.gvm.product.gvmpoin.module.common.PsJsonView;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tier_system_level")
@JsonInclude(JsonInclude.Include.NON_NULL)
@lombok.Data
public class TierSystemLevel {

  @Id
  @SequenceGenerator(
      name = "tier_system_level_seq",
      sequenceName = "tier_system_level_id_seq",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.AUTO,
      generator = "tier_system_level_seq")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "tier_system_master_id")
  private TierSystemMaster tierSystemMaster;

  @NotNull
  @JsonView(PsJsonView.TierSystemConsumerProgress.class)
  private Integer level;

  @NotNull
  @JsonView(PsJsonView.TierSystemConsumerProgress.class)
  private String name;

  @JsonView(PsJsonView.TierSystemConsumerProgress.class)
  private String description;

  @NotNull
  @JsonProperty("level_point_required")
  @JsonView(PsJsonView.TierSystemConsumerProgress.class)
  private Integer levelPointRequired;
}
