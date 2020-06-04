package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import lombok.Data;

import java.util.List;

@Data
public class TadaBrand {

  private String brand;
  private List<TadaProgram> programs;
}
