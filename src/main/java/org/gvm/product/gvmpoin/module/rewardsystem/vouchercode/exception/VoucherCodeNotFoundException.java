package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class VoucherCodeNotFoundException extends GlobalPoinException {

  public VoucherCodeNotFoundException(String voucherCode) {
    super(String.format("Voucher Code %s Not Found !", voucherCode));
  }

}
