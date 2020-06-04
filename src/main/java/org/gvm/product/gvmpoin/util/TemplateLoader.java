package org.gvm.product.gvmpoin.util;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@Component
public class TemplateLoader {

  @Autowired
  private ResourceLoader resourceLoader;

  /**
   * Construct HTML File to String
   *
   * @param templateName HTML File String Parameter (ex: "index.html")
   * @return constructed HTML String
   */
  public String load(String templateName) {

    Resource resource = resourceLoader.getResource("classpath:/" + templateName);

    InputStream inputStream = null;
    try {
      inputStream = resource.getInputStream();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    StringWriter stringWriter = new StringWriter();
    try {
      IOUtils.copy(inputStream, stringWriter, StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return stringWriter.toString();
  }
}
