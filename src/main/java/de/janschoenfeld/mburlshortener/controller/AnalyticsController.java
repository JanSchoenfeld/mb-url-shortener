package de.janschoenfeld.mburlshortener.controller;

import de.janschoenfeld.mburlshortener.model.repository.UrlRepository;
import de.janschoenfeld.mburlshortener.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("analytics")
@RequiredArgsConstructor
public class AnalyticsController {

  private final AnalyticsService analyticsService;
  private final UrlRepository urlRepository;

  @GetMapping("clicks/day")
  public Long getDailyClicks(@RequestParam(required = false) String shortenedUrl) {
    if (StringUtils.isNotEmpty(shortenedUrl)) {
      return analyticsService.getDailyClicks(shortenedUrl);
    }
    return urlRepository.sumDailyClicks();
  }

}
