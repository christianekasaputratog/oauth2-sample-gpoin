package org.gvm.product.gvmpoin.module.microsite;

import lombok.Data;

@Data
public class PaninMicrosite {

  private static final String FORMAT = "%s|%s|%s|%s|%s";

  private String name;
  private String domicile;
  private String occupation;
  private String incomeRange;
  private String telephoneNumber;

  public PaninMicrosite(String name, String domicile, String occupation, String incomeRange,
      String telephoneNumber) {
    this.name = name;
    this.domicile = domicile;
    this.occupation = occupation;
    this.incomeRange = incomeRange;
    this.telephoneNumber = telephoneNumber;
  }

  public String toFormattedString() {
    return String.format(FORMAT, name, domicile, occupation, incomeRange, telephoneNumber);
  }
}
