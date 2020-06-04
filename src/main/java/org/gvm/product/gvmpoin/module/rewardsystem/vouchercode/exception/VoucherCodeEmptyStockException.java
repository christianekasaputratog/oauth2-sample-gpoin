package org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.exception;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class VoucherCodeEmptyStockException extends GlobalPoinException {

  public VoucherCodeEmptyStockException() {
    super("All voucher code has been used");
  }

}
