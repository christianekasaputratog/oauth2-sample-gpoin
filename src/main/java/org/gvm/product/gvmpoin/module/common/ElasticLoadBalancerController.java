package org.gvm.product.gvmpoin.module.common;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElasticLoadBalancerController {

  @RequestMapping(value = "/health_check")
  public String greeting(@RequestParam(value = "name", defaultValue = "Sofian") String name) {
    return "ok";
  }
}
