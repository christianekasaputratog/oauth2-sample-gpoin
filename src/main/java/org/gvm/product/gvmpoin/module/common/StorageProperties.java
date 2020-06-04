package org.gvm.product.gvmpoin.module.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by sofian-hadianto on 7/21/17.
 */
@ConfigurationProperties("storage")
public class StorageProperties {

  /**
   * Folder location for storing files.
   */
  private String location = "upload-dir";

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
