package org.gvm.product.gvmpoin.module.common;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

  @GetMapping(value = {"", "/"})
  public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
    return "ps_index";
  }

  /**
   * Generate Password .
   *
   * @return (String) encodedNewPassword
   */
  @GetMapping("genpass")
  @ResponseBody
  public String generatePass(@RequestParam(value = "str") String password) {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    return passwordEncoder.encode(password);
  }

  /**
   * Generate Hash .
   *
   * @param psId Unique Poin System Id
   * @return (String) generatedHash of PS ID
   */
  @GetMapping("genhash")
  @ResponseBody
  public String generateHash(@RequestParam(value = "str") String psId) {
    String salt = "Aha9yeWq4WP3zGh0eKW5l2wehwz/XRCf8CijBKcqLbNZ";

    Md5PasswordEncoder md5Encoder = new Md5PasswordEncoder();
    return md5Encoder.encodePassword(psId, salt);
  }

}
