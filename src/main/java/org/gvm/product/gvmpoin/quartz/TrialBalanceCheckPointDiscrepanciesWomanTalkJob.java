package org.gvm.product.gvmpoin.quartz;

import org.gvm.product.gvmpoin.module.trialbalance.TrialBalanceService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TrialBalanceCheckPointDiscrepanciesWomanTalkJob implements Job {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private TrialBalanceService trialBalanceService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    logger.info("Start executing Job :: Trial Balance :: Check Point Discrepancies WomanTalk");

    trialBalanceService.checkPoinDiscrepanciesFromWomanTalk();

    logger.info("Finish executing Job :: Trial Balance :: Check Point Discrepancies WomanTalk");
  }
}
