package org.gvm.product.gvmpoin.configuration.spring;

import com.netflix.appinfo.AmazonInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EurekaConfiguration {

  @Value("${server.port:80}")
  private int port;

  /**
   * @param inetUtils .
   * @return Eureka Instance Config Bean
   */
  @Bean
  @Profile("!dev")
  public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
    EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(inetUtils);
    AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
    config.setDataCenterInfo(info);
    info.getMetadata().put(
        AmazonInfo.MetaDataKey.publicHostname.getName(),
        info.get(AmazonInfo.MetaDataKey.publicIpv4));
    config.setHostname(info.get(AmazonInfo.MetaDataKey.publicHostname));
    config.setIpAddress(info.get(AmazonInfo.MetaDataKey.publicIpv4));
    config.setNonSecurePort(port);
    return config;
  }
}
