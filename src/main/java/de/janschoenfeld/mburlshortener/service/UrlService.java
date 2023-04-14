package de.janschoenfeld.mburlshortener.service;

import org.apache.commons.lang3.StringUtils;

public class UrlService {

  public String shortenUrl(String url, String target) {
    if (StringUtils.isEmpty(target)) {
      return shortenRandom(url);
    }
    return shortenToTarget(url, target);
  }

  private String shortenRandom(String url) {
    return null;
  }

  private String shortenToTarget(String url, String target) {
    //check if target exists
    return null;
  }




}
