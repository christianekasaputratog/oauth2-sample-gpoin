package org.gvm.product.gvmpoin.module.rewardsystem.tada;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@DisallowConcurrentExecution
public class TadaEGiftSynchronizationPointJob implements Job {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private TadaSynchronizationService tadaSynchronizationService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    logger.info("Start executing Job :: TadaEGiftSynchronization :: synchronizeEGift");

    tadaSynchronizationService.synchronizeEGift();

    logger.info("Finish executing Job :: TadaEGiftSynchronization :: synchronizeEGift");
  }
}
