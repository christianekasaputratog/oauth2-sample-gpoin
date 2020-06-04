package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class SepulsaLengthPhoneNumberException extends GlobalPoinException {

  public SepulsaLengthPhoneNumberException() {
    super("Phone Number's Length Must Between 9 and 13 !");
  }

}
