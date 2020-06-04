package org.gvm.product.gvmpoin.quartz;

import org.gvm.product.gvmpoin.module.continuousengagement.ContinuousEngagementService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ContinuousEngagementResetCountJob implements Job {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private ContinuousEngagementService continuousEngagementService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    logger.info("Start executing Job :: Continuous Engagement :: Reset Count");

    continuousEngagementService.resetCountToZeroWhenConsumerIdleInConsecutiveDays();

    logger.info("Finish executing Job :: Continuous Engagement :: Reset Count");
  }
}
