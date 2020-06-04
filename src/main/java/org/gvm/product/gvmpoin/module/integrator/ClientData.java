package org.gvm.product.gvmpoin.module.integrator;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientData {

  @Id
  @GeneratedValue(generator = "clientdata_seq")
  @SequenceGenerator(name = "clientdata_seq", sequenceName = "clientdata_seq_id_seq",
      allocationSize = 1)
  private Long id;

  private String client;

  private String email;

  @JsonProperty("fullName")
  private String fullName;

  @JsonProperty("email_verified")
  private Integer emailVerified;

  private Integer point;

  private Date birthday;

}
