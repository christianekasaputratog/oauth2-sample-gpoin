package org.gvm.product.gvmpoin.module.consumer;

public class ConsumerPasswordStrengthChecker {

  /**
   * Calculate Strength Percentage of password .
   *
   * @param password GPoin Member's Password
   * @return Strength Percentage
   */
  public static int checkPercentage(String password) {
    int strengthPercentage = 0;

    String[] partialRegexChecks = {
        ".*[a-z]+.*", // lower
        ".*[A-Z]+.*", // upper
        ".*[\\d]+.*", // digits
        ".*[`~@#%&:;\"',/\\<\\(\\[\\{\\^\\-\\=\\$\\!\\|\\]\\}\\)\\?\\*\\+\\.\\>]+.*" // symbols
    };

    for (String regexCheck : partialRegexChecks) {
      if (password.matches(regexCheck)) {
        strengthPercentage += 25;
      }
    }

    return strengthPercentage;
  }
}
