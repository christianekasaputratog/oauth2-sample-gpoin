package org.gvm.product.gvmpoin.module.client;

import lombok.Getter;

@Getter
public enum ClientIds {

  WOMANTALK(2, "womantalk"),
  BOLALOB(3, "bolalob");

  private int value;
  private String phrase;

  ClientIds(int value, String phrase) {
    this.value = value;
    this.phrase = phrase;
  }

}
