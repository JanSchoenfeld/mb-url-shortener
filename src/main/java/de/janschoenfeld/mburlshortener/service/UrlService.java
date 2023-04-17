package de.janschoenfeld.mburlshortener.service;

import de.janschoenfeld.mburlshortener.model.Url;
import de.janschoenfeld.mburlshortener.model.repository.UrlRepository;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UrlService {

  private final UrlRepository urlRepository;

  public String shortenUrl(String url, String target, int characterLimit) {
    if (StringUtils.isEmpty(target)) {
      return shortenRandom(url, characterLimit);
    }
    return shortenToTarget(url, target);
  }

  private String shortenRandom(String url, int length) {
    String alreadySavedUrl = checkOriginalUrlCollision(url);
    if (alreadySavedUrl != null) {
      return alreadySavedUrl;
    }

    var shorted = buildShortedUrl(url, length);
    shorted = checkIfShortUrlExists(shorted);

    var urlEntity = new Url(url, shorted);
    urlRepository.save(urlEntity);
    return urlEntity.getShorted();
  }

  private String shortenToTarget(String url, String target) {
    if (urlRepository.findByShorted(target).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A shorted URL with the desired name already exists");
    }
    var urlEntity = new Url(url, target);
    urlRepository.save(urlEntity);
    return urlEntity.getShorted();
  }

  private String checkOriginalUrlCollision(String url) {
    return urlRepository.findFirstByOriginal(url).map(Url::getShorted).orElse(null);
  }

  /**
   * Collision chances for the encoded and hashed shorted url are very slim, if one should happen we slightly alter
   * the shorted URL by removing the last character from it.
   * Another approach would be to salt the hash with another random value.
   */
  private String checkIfShortUrlExists(String shorted) {
    if (urlRepository.findByShorted(shorted).isPresent()) {
      return shorted.substring(0, shorted.length() - 1);
    }
    return shorted;
  }

  public void incrementCounter(Url url) {
    url.setTimesCalled_day(url.getTimesCalled_day() + 1);
    urlRepository.save(url);
  }

  public static String buildShortedUrl(String url, int length) {
    var hash = new DigestUtils("MD5").digestAsHex(url);
    var result = Base64.encodeBase64URLSafeString(hash.getBytes(StandardCharsets.UTF_8));
    return result.substring(0, length);
  }
}
