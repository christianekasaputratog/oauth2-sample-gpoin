package org.gvm.product.gvmpoin.util;

import org.gvm.product.gvmpoin.module.common.Constant;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import java.util.Random;

/**
 * Created by sofian-hadianto on 3/15/17.
 */
public class HashUtil {

  public static String getHash(String psId) {
    Md5PasswordEncoder md5Encoder = new Md5PasswordEncoder();
    return md5Encoder.encodePassword(psId, Constant.SALT);
  }

  /**
   * @return generatedPin (Random 6 number).
   */
  public static String generateNewPassword() {
    String sequenceNumber = "1234567890";
    char[] chars = sequenceNumber.toCharArray();
    Random random = new Random();
    StringBuilder stringBuilder = new StringBuilder();
    int pinlength = 6;
    for (int i = 0; i < pinlength; i++) {
      char character = chars[random.nextInt(chars.length)];
      stringBuilder.append(character);
    }
    return stringBuilder.toString();
  }
}
