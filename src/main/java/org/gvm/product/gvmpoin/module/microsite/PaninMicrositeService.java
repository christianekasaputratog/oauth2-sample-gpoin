package org.gvm.product.gvmpoin.module.microsite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaninMicrositeService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public void save(PaninMicrositeForm paninMicrosite) {
    logger.info(paninMicrosite.toFormattedString());
  }
}
