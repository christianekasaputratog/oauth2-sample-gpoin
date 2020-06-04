package org.gvm.product.gvmpoin.module.microsite;

/*
 * Created by sofian-hadianto on 4/18/17.
 */

import org.hibernate.validator.constraints.NotEmpty;

public abstract class MicrositeRecaptchaForm {

  @NotEmpty
  private String recaptchaResponse;

  public void setRecaptchaResponse(String response) {
    this.recaptchaResponse = response;
  }

  public String getRecaptchaResponse() {
    return recaptchaResponse;
  }

  @Override
  public String toString() {
    return "RecaptchaForm{" + "recaptchaResponse='" + recaptchaResponse + '\'' + '}';
  }
}
