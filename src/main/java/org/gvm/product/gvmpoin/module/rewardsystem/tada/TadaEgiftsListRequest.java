package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TadaEgiftsListRequest {

  private String merchant;
  @JsonProperty("program_ids")
  private List<String> programIds;
}
