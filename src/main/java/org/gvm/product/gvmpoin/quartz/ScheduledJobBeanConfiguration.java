package org.gvm.product.gvmpoin.quartz;

import org.gvm.product.gvmpoin.module.rewardsystem.tada.TadaEGiftSynchronizationPointJob;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

@Configuration
public class ScheduledJobBeanConfiguration {

  private static final String CONTINUOUS_ENGAGEMENT_RESET_COUNT_JOB =
      "Job-Continuous-Engagement-Reset-Count";
  private static final String CONTINUOUS_ENGAGEMENT_RESET_COUNT_TRIGGER =
      "Trigger-Continuous-Engagement-Reset-Count";
  private static final String CONTINUOUS_ENGAGEMENT_RESET_COUNT_CRON_EXPRESSION = "0 0 0 ? * *";

  private static final String TRIAL_BALANCE_POINT_DISCREPANCIES_CHECK_WOMANTALK_JOB =
      "Job-Trial-Balance-Point-Discrepancies-Check-Womantalk";
  private static final String TRIAL_BALANCE_POINT_DISCREPANCIES_CHECK_WOMANTALK_TRIGGER =
      "Trigger-Trial-Balance-Point-Discrepancies-Check-Womantalk";
  private static final String TRIAL_BALANCE_POINT_DISCREPANCIES_CHECK_WOMANTALK_CRON_EXPRESSION =
      "0 0 2 ? * *";

  private static final String TADA_EGIFT_SYNCHRONIZATION_JOB = "Job-Tada-EGift-Synchronization";
  private static final String TADA_EGIFT_SYNCHRONIZATION_TRIGGER =
      "Trigger-Tada-EGift-Synchronization";
  private static final String TADA_EGIFT_SYNCHRONIZATION_START_TIME = "2000-01-01 00:00:00.0";
  private static final SimpleDateFormat TADA_EGIFT_SYNCHRONIZATION_DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
  private static final int TADA_EGIFT_SYNCHRONIZATION_INTERVAL_IN_HOURS = 4;

  @Autowired
  private SchedulerFactoryBean schedulerFactory;

  @Bean(name = CONTINUOUS_ENGAGEMENT_RESET_COUNT_JOB)
  public JobDetailFactoryBean continuousEngagementResetCountJob() {
    return SchedulerComponent.createJobDetail(ContinuousEngagementResetCountJob.class);
  }

  @Bean(name = CONTINUOUS_ENGAGEMENT_RESET_COUNT_TRIGGER)
  public CronTriggerFactoryBean continuousEngagementResetCountTrigger(
      @Qualifier(CONTINUOUS_ENGAGEMENT_RESET_COUNT_JOB) JobDetail jobDetail) {
    return SchedulerComponent.createCronTrigger(jobDetail,
        CONTINUOUS_ENGAGEMENT_RESET_COUNT_CRON_EXPRESSION);
  }

  @Bean(name = TRIAL_BALANCE_POINT_DISCREPANCIES_CHECK_WOMANTALK_JOB)
  public JobDetailFactoryBean trialBalancePointDiscrepanciesCheckWomantalkJob() {
    return SchedulerComponent
        .createJobDetail(TrialBalanceCheckPointDiscrepanciesWomanTalkJob.class);
  }

  @Bean(name = TRIAL_BALANCE_POINT_DISCREPANCIES_CHECK_WOMANTALK_TRIGGER)
  public CronTriggerFactoryBean trialBalancePointDiscrepanciesCheckWomantalkTrigger(
      @Qualifier(TRIAL_BALANCE_POINT_DISCREPANCIES_CHECK_WOMANTALK_JOB) JobDetail jobDetail) {
    return SchedulerComponent.createCronTrigger(
        jobDetail, TRIAL_BALANCE_POINT_DISCREPANCIES_CHECK_WOMANTALK_CRON_EXPRESSION);
  }

  @PostConstruct
  public void scheduleEGiftSynchonizationJob() throws ParseException, SchedulerException {
    final Date startTime = TADA_EGIFT_SYNCHRONIZATION_DATE_FORMAT
        .parse(TADA_EGIFT_SYNCHRONIZATION_START_TIME);

    JobDetail jobDetail = SchedulerComponent.createJobDetail(
        TADA_EGIFT_SYNCHRONIZATION_JOB, TadaEGiftSynchronizationPointJob.class);

    Trigger trigger = SchedulerComponent.createCalendarIntervalTrigger(
        TADA_EGIFT_SYNCHRONIZATION_TRIGGER,
        jobDetail,
        startTime,
        TADA_EGIFT_SYNCHRONIZATION_INTERVAL_IN_HOURS);

    SchedulerComponent.scheduleJobWithTrigger(
        schedulerFactory, TADA_EGIFT_SYNCHRONIZATION_TRIGGER, jobDetail, trigger);
  }
}
