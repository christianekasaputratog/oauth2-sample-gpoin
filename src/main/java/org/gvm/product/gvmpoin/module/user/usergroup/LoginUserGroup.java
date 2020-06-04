package org.gvm.product.gvmpoin.module.user.usergroup;

import com.fasterxml.jackson.annotation.JsonView;

import org.gvm.product.gvmpoin.module.common.PsJsonView;
import org.gvm.product.gvmpoin.util.DateUtil;

import lombok.Data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "cs_login_group")
@Data
public class LoginUserGroup {

  @Id
  @JsonView({PsJsonView.User.class, PsJsonView.UserGroup.class})
  @GeneratedValue(generator = "user_cs_group_seq")
  @SequenceGenerator(name = "user_cs_group_seq", sequenceName = "cs_login_group_id_seq",
      allocationSize = 1)
  private long id;

  @NotNull
  @JsonView({PsJsonView.User.class, PsJsonView.UserGroup.class})
  @Column(unique = true)
  private String name;

  @JsonView(PsJsonView.UserGroup.class)
  private Date createdTime;

  @JsonView(PsJsonView.UserGroup.class)
  private Date updatedTime;

  private String groupCreator;

  private String groupModifier;

  @JsonView(PsJsonView.UserGroup.class)
  private String groupStatus;

  @PrePersist
  public void onCreate() {
    createdTime = DateUtil.getTimeNow();
  }

  @PreUpdate
  public void onUpdate() {
    updatedTime = DateUtil.getTimeNow();
  }
}
