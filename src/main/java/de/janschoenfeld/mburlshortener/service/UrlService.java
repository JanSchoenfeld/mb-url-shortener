package de.janschoenfeld.mburlshortener.service;

import de.janschoenfeld.mburlshortener.model.Url;
import de.janschoenfeld.mburlshortener.model.repository.UrlRepository;
import de.janschoenfeld.mburlshortener.util.ShortenUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

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

    var shorted = ShortenUtils.buildShortenedUrl(url, length);
    shorted = checkIfShortUrlExists(shorted);

    var urlEntity = new Url(url, shorted);
    urlRepository.save(urlEntity);
    return buildResponseUrl(urlEntity.getShorted());
  }

  private String shortenToTarget(String url, String target) {
    if (urlRepository.findByShorted(target).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A shortened URL with the desired name already exists");
    }
    var urlEntity = new Url(url, target);
    urlRepository.save(urlEntity);
    return buildResponseUrl(urlEntity.getShorted());
  }

  private String checkOriginalUrlCollision(String url) {
    final var savedUrlOptional = urlRepository.findFirstByOriginal(url);
    return savedUrlOptional.map(value -> buildResponseUrl(value.getShorted())).orElse(null);
  }

  /**
   * collision chances for the encoded and hashed shortened url are very slim, if one should happen we slightly alter
   * the shortened url
   */
  private String checkIfShortUrlExists(String shortened) {
    if (urlRepository.findByShorted(shortened).isPresent()) {
      return shortened.substring(0, shortened.length() - 1);
    }
    return shortened;
  }

  private String buildResponseUrl(String shorted) {
    UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath().path(shorted);
    return builder.build().toUriString();
  }

  public void incrementCounter(Url url) {
    url.setTimesCalled_day(url.getTimesCalled_day() + 1);
    urlRepository.save(url);
  }
}
