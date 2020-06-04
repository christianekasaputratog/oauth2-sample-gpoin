package org.gvm.product.gvmpoin.module.client;

import javax.validation.constraints.NotNull;

public class ClientApproveRequest {

  @NotNull
  private final Long id;

  public ClientApproveRequest() {
    this.id = null;
  }

  public ClientApproveRequest(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

}
