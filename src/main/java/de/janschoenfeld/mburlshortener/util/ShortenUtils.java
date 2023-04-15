package de.janschoenfeld.mburlshortener.util;

import org.apache.commons.codec.digest.DigestUtils;

public class ShortenUtils {

  private ShortenUtils(){}

  private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final int BASE62 = 62;

  public static String buildShortenedUrl(String url, int length) {
    var hash = new DigestUtils("MD5").digestAsHex(url);
    var result = base62Encode(hash);
    return result.substring(0, length);
  }

  public static String base62Encode(String input) {
    long decimal = 0;
    for (int i = 0; i < input.length(); i++) {
      decimal = decimal * 256 + input.charAt(i);
    }
    StringBuilder sb = new StringBuilder();
    while (decimal > 0) {
      int digit = (int) (decimal % BASE62);
      sb.append(BASE62_CHARS.charAt(digit));
      decimal /= BASE62;
    }
    return sb.reverse().toString();
  }
}


