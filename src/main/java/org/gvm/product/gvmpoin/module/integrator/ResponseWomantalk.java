package org.gvm.product.gvmpoin.module.integrator;

import java.util.List;

public class ResponseWomantalk {

  private Integer status;
  private List<ClientData> data;

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public List<ClientData> getData() {
    return data;
  }

  public void setData(List<ClientData> data) {
    this.data = data;
  }
}
