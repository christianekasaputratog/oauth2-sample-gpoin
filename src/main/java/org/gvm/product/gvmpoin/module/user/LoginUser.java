package org.gvm.product.gvmpoin.module.user;

import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.module.rewardsystem.partner.Partner;
import org.gvm.product.gvmpoin.module.user.usergroup.LoginUserGroup;
import org.gvm.product.gvmpoin.util.DateUtil;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "cs_login")
@Data
public class LoginUser {

  @Id
  @GeneratedValue(generator = "user_cs_seq")
  @SequenceGenerator(name = "user_cs_seq", sequenceName = "user_cs_seq_id_seq", allocationSize = 1)
  private long id;

  @NotNull
  @JsonView(PsJsonView.User.class)
  @Column(unique = true)
  private String username;

  @NotNull
  private String password;

  @JsonView(PsJsonView.User.class)
  private String name;

  private Integer status;

  private String role;

  @JsonView(PsJsonView.User.class)
  private String imageUrl;

  @JsonView(PsJsonView.User.class)
  private Date createdTime;

  @JsonView(PsJsonView.User.class)
  private Date updatedTime;

  private Date removedTime;

  private String userCreator;

  private String userModifier;

  private String userRemover;

  @ManyToOne
  @JoinColumn(name = "login_user_group")
  @JsonView(PsJsonView.User.class)
  private LoginUserGroup loginUserGroup;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "user_partner", joinColumns = @JoinColumn(name = "user_id",
      referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "partner_id",
      referencedColumnName = "id"))
  @JsonView(PsJsonView.User.class)
  private List<Partner> partners = new ArrayList<>();

  @JsonView(PsJsonView.User.class)
  private Integer userStatus;

  @PrePersist
  void onCreate() {
    createdTime = DateUtil.getTimeNow();
  }

  @PreUpdate
  public void onUpdate() {
    updatedTime = DateUtil.getTimeNow();
  }
}
