package org.gvm.product.gvmpoin.configuration.security;

public enum AuthScope {
  SUPERADMIN, READ, WRITE, CLIENT;

  @Override
  public String toString() {
    return super.toString().toLowerCase();
  }
}
