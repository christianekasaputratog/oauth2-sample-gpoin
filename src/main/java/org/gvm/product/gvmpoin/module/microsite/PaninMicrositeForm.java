package org.gvm.product.gvmpoin.module.microsite;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Created by sofian-hadianto on 4/19/17.
 */
public class PaninMicrositeForm extends MicrositeRecaptchaForm {

  private static final String FORMAT = "%s|%s|%s|%s|%s|%s";

  @NotEmpty
  @Size(min = 1, max = 255)
  private String name;

  @NotEmpty
  @Size(min = 1, max = 255)
  private String domicile;

  @NotEmpty
  @Size(min = 1, max = 255)
  private String occupation;

  @NotEmpty
  @Size(min = 1, max = 255)
  private String incomeRange;

  @NotEmpty
  @Size(min = 1, max = 255)
  private String phone;

  @NotEmpty
  @Size(min = 1, max = 255)
  private String referrer;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDomicile() {
    return domicile;
  }

  public void setDomicile(String domicile) {
    this.domicile = domicile;
  }

  public String getOccupation() {
    return occupation;
  }

  public void setOccupation(String occupation) {
    this.occupation = occupation;
  }

  public String getIncomeRange() {
    return incomeRange;
  }

  public void setIncomeRange(String incomeRange) {
    this.incomeRange = incomeRange;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getReferrer() {
    return referrer;
  }

  public void setReferrer(String referrer) {
    this.referrer = referrer;
  }

  public String toFormattedString() {
    return String.format(FORMAT, name, domicile, occupation, incomeRange, phone, referrer);
  }
}
