package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TadaProgram {

  private Long id;

  private String image;

  @JsonProperty("item_name")
  private String itemName;

  @JsonProperty("master_program")
  private String masterProgram;

  @JsonProperty("program_id")
  private String programId;

  @JsonProperty("program_name")
  private String programName;

  @JsonProperty("term_condition")
  private String termCondition;

  private Integer value;

  private volatile String brand;

  volatile boolean alreadySync;
}
