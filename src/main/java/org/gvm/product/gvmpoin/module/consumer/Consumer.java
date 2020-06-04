package org.gvm.product.gvmpoin.module.consumer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.client.Client;
import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.consumer.socialmedia.ConsumerSocialMedia;
import org.gvm.product.gvmpoin.module.trialbalance.TrialBalance;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
//@JsonInclude(JsonInclude.Include.NON_NULL)
@lombok.Data
public class Consumer {

  @Id
  @GeneratedValue(
      generator = "consumernew_seq")
  @SequenceGenerator(
      name = "consumernew_seq",
      sequenceName = "consumernew_seq_id_seq",
      allocationSize = 1)
  @JsonView(PsJsonView.Consumer.class)
  private Long id;

  @NotNull
  @Size(max = 12)
  @Column(unique = true)
  @JsonProperty("ps_id")
  @JsonView(value = {PsJsonView.Consumer.class, PsJsonView.MyProgress.class,
      PsJsonView.ConsumerWithBalance.class})
  private String psId;

  @JsonView(value = {PsJsonView.Consumer.class, PsJsonView.MyProgress.class,
      PsJsonView.ConsumerWithBalance.class})
  private String name = "";

  @JsonView(value = {PsJsonView.Consumer.class, PsJsonView.MyProgress.class,
      PsJsonView.ConsumerWithBalance.class})
  @JsonProperty("real_name")
  private String realName = "";

  private String password;

  private String temporaryPassword;

  @Transient
  private String passwordConfirmation;

  //  @Column(unique = true)
  @JsonView(value = {PsJsonView.Consumer.class, PsJsonView.MyProgress.class,
      PsJsonView.ConsumerWithBalance.class})
  private String email = "";

  private Integer status;

  @Temporal(TemporalType.TIMESTAMP)
  @JsonProperty("register_time")
  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  private Date registerTime;

  private Date updatedTime;

  @JsonProperty("hash_code")
  private String hashCode;

  @Transient
  @JsonView(PsJsonView.Consumer.class)
  private String hash;

  @ManyToOne
  @JoinColumn(name = "register_from")
  @JsonProperty("register_from")
  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  private Client registerFrom;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "trial_balance_id")
  @JsonProperty("trialbalance")
  @JsonView({PsJsonView.ConsumerWithBalance.class})
  private TrialBalance trialBalance;

  @JsonProperty("emailVerified")
  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  private Boolean emailVerified;

  private String emailVerificationCode;

  private Date emailVerificationCodeCreatedTime;

  @JsonProperty("facebook_id")
  private String facebookId;
  
  @JsonProperty("twitter_id")
  private String twitterId;

  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  @JsonProperty("date_of_birth")
  private Timestamp dateOfBirth;

  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  private String gender;

  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  @JsonProperty("phone_number")
  private String phoneNumber;

  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  private String address;

  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  @JsonProperty("postal_code")
  private Integer postCode;

  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  private String city;

  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  @JsonProperty("identity_number")
  private String identityNumber;

  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  @JsonProperty("identity_image")
  private String identityImage;

  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  @JsonProperty("tax_image")
  private String taxImage;

  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  private String hobby;

  @OneToOne
  @JoinColumn(name = "social_media_id")
  @JsonView({PsJsonView.Consumer.class, PsJsonView.ConsumerWithBalance.class})
  @JsonProperty("social_media")
  private ConsumerSocialMedia consumerSocialMedia;

  @PrePersist
  public void onCreate() {
    this.registerTime = new Date();
    this.updatedTime = new Date();
  }
}