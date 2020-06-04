package org.gvm.product.gvmpoin.quartz;

import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Date;

public class SchedulerComponent {

  public static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail,
      String cronExpression) {
    CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
    factoryBean.setJobDetail(jobDetail);
    factoryBean.setCronExpression(cronExpression);
    factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
    return factoryBean;
  }

  public static JobDetailFactoryBean createJobDetail(Class jobClass) {
    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
    factoryBean.setJobClass(jobClass);
    // job has to be durable to be stored in DB:
    factoryBean.setDurability(true);
    return factoryBean;
  }

  public static JobDetail createJobDetail(String identity, Class<? extends Job> jobClass) {
    return JobBuilder.newJob(jobClass)
        .storeDurably()
        .withIdentity(identity)
        .build();
  }

  public static Trigger createCalendarIntervalTrigger(
      String identity, JobDetail jobDetail, Date startTime, int intervalInHours) {

    return TriggerBuilder.newTrigger()
        .forJob(jobDetail)
        .withIdentity(identity)
        .withSchedule(
            CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                .withIntervalInHours(intervalInHours)
                .withMisfireHandlingInstructionDoNothing()
        )
        .startAt(startTime)
        .build();
  }

  public static void scheduleJobWithTrigger(
      SchedulerFactoryBean schedulerFactory, String identity, JobDetail jobDetail,
      Trigger trigger)
      throws SchedulerException {
    final Scheduler scheduler = schedulerFactory.getScheduler();

    scheduler.addJob(jobDetail, true);

    TriggerKey triggerKey = new TriggerKey(identity);

    boolean triggerExists = scheduler.checkExists(triggerKey);
    if (triggerExists) {
      Trigger oldTrigger = scheduler.getTrigger(triggerKey);
      scheduler.rescheduleJob(oldTrigger.getKey(), trigger);
    } else {
      scheduler.scheduleJob(trigger);
    }
  }
}
