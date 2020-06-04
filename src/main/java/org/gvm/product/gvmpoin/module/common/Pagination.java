package org.gvm.product.gvmpoin.module.common;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Pagination {

  private static int first = 1;

  private int previous;

  private int current;

  private int next;

  private int last;

  private int startLoop;

  private int lastLoop;

}
