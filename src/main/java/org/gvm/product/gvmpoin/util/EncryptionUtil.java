package org.gvm.product.gvmpoin.util;

import org.gvm.product.gvmpoin.module.rewardsystem.vouchercode.sepulsa.SepulsaIntegrationException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {

  private static final String CHARSET_UT8 = "UTF-8";
  private static final String ALG_AES = "AES";
  private static final String CIPHER_AES_PKCS5 = "AES/ECB/PKCS5Padding";

  @Value("${gpoin.key}")
  private static String gpoinEncryptionKey;

  public static String encode(String str, String key) {
    try {
      SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(CHARSET_UT8), ALG_AES);
      Cipher cipher = Cipher.getInstance(CIPHER_AES_PKCS5);
      cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

      byte[] encrypted = cipher.doFinal(str.getBytes(CHARSET_UT8));
      return new String(java.util.Base64.getEncoder().encode(encrypted), CHARSET_UT8);
    } catch (Exception e) {
      throw new SepulsaIntegrationException(e.getMessage());
    }
  }

  private static String decode(String encryptedString, String key) {
    try {
      SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(CHARSET_UT8), ALG_AES);
      Cipher cipher = Cipher.getInstance(CIPHER_AES_PKCS5);
      cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

      byte[] cipherText = java.util.Base64.getDecoder()
          .decode(encryptedString.getBytes(CHARSET_UT8));
      return new String(cipher.doFinal(cipherText), CHARSET_UT8);
    } catch (Exception e) {
      throw new SepulsaIntegrationException(e.getMessage());
    }
  }

  public static JSONObject getObjectDecodedData(MultiValueMap<String, String> entity) {
    String decodedData = decode(entity.getFirst("data"), gpoinEncryptionKey);
    return new JSONObject(decodedData);
  }

}