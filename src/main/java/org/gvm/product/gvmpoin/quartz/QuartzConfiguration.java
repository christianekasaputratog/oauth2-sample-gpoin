package org.gvm.product.gvmpoin.quartz;

import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

@Configuration
public class QuartzConfiguration {

  private static final String SCHEDULER_NAME = "PointSystem-Scheduler-Core";
  private static final String QUARTZ_PROPERTIES = "quartz.properties";

  @Autowired
  List<Trigger> listOfTrigger;

  @Bean
  public SpringBeanJobFactory springBeanJobFactory(ApplicationContext applicationContext) {
    AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
    jobFactory.setApplicationContext(applicationContext);
    return jobFactory;
  }

  @Bean(name = SCHEDULER_NAME)
  public SchedulerFactoryBean schedulerFactoryBean(
      DataSource dataSource,
      PlatformTransactionManager transactionManager,
      SpringBeanJobFactory springBeanJobFactory) throws IOException {

    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    factory.setOverwriteExistingJobs(true);
    factory.setAutoStartup(true);
    factory.setDataSource(dataSource);
    factory.setJobFactory(springBeanJobFactory);
    factory.setQuartzProperties(quartzProperties());
    factory.setWaitForJobsToCompleteOnShutdown(true);
    factory.setTransactionManager(transactionManager);

    // Here we will set all the trigger beans we have defined.
    if (listOfTrigger != null && !listOfTrigger.isEmpty()) {
      factory.setTriggers(listOfTrigger.toArray(new Trigger[listOfTrigger.size()]));
    }

    return factory;
  }

  @Bean
  public Properties quartzProperties() throws IOException {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    propertiesFactoryBean.setLocation(new ClassPathResource(QUARTZ_PROPERTIES));
    propertiesFactoryBean.afterPropertiesSet();
    return propertiesFactoryBean.getObject();
  }
}
