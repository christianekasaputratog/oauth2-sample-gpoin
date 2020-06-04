package org.gvm.product.gvmpoin.module.common.exception;

public class PsIdTemporarySuspendedException extends GlobalPoinException {

  private static final long serialVersionUID = -2702619064270147192L;

  public PsIdTemporarySuspendedException(String psId) {
    super(String.format("Ps Id %s has been suspended.", psId));
  }
}
