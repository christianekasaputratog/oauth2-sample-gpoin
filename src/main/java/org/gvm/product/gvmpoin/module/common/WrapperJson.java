package org.gvm.product.gvmpoin.module.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WrapperJson {

  private Integer status;

  private JsonData data;

}
