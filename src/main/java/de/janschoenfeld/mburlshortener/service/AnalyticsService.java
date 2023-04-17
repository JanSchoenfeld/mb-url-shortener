package de.janschoenfeld.mburlshortener.service;

import de.janschoenfeld.mburlshortener.model.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

  private final UrlRepository urlRepository;

  public Long getDailyClicks(String shorted) {
    var url = urlRepository.findByShorted(shorted).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Please provide an existing shorted Url"));

    return url.getTimesCalled_day();
  }

  @Scheduled(cron = "${analytics.ttl.cron.expression}")
  public void resetDailyClicks() {
    log.info("Reset daily clicks");
    urlRepository.resetDailyClicks();
  }
}

