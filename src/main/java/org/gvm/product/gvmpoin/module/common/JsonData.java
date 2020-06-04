package org.gvm.product.gvmpoin.module.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonData {

  private String email;

  private String full_name;

  private Integer email_verified;

  private Integer point;

  private Date birthday;

}
