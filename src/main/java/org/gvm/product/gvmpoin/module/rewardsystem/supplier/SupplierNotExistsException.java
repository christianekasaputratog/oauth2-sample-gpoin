package org.gvm.product.gvmpoin.module.rewardsystem.supplier;

import org.gvm.product.gvmpoin.module.common.exception.GlobalPoinException;

public class SupplierNotExistsException extends GlobalPoinException {

  public SupplierNotExistsException(Long supplierId) {
    super(String.format("Supplier %s not exists.", supplierId));
  }

}
