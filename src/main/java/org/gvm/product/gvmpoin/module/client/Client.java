package org.gvm.product.gvmpoin.module.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.PsJsonView;

import lombok.Data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Client {

  @Id
  @GeneratedValue(generator = "clientnew_seq")
  @SequenceGenerator(
      name = "clientnew_seq",
      sequenceName = "clientnew_seq_id_seq",
      allocationSize = 1)
  private Long id;

  @NotNull
  @Column(unique = true)
  private String clientId;
  private String webServerRedirectUri;

  @NotNull
  private String clientSecret;
  private String email;

  @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP")
  @Temporal(TemporalType.TIMESTAMP)
  private Date registrationTime;

  private Boolean approved = false;

  private Boolean emailVerificationForced = Boolean.FALSE;

  @JsonView({PsJsonView.Journal.class, PsJsonView.Consumer.class,
      PsJsonView.ConsumerWithBalance.class})
  private String name;

  @JsonView(PsJsonView.Journal.class)
  private String logo;

  @JsonView(PsJsonView.Journal.class)
  private String icon;

  private Integer status;

  private Integer dailyPointCap;

  @PrePersist
  void onCreate() {
    this.approved = false;
    this.registrationTime = new Date();
  }
}
