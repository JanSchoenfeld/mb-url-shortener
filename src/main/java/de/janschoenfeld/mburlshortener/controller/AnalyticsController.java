package de.janschoenfeld.mburlshortener.controller;

import de.janschoenfeld.mburlshortener.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("analytics")
@RequiredArgsConstructor
public class AnalyticsController {

  private final AnalyticsService analyticsService;

  @GetMapping("clicks/day")
  @Operation(summary = "Returns all clicks for the shorted URL on this day, "
                       + "if no specified aggregates all clicks over all links")
  public Long getDailyClicks(@RequestParam String shortedUrl) {
    return analyticsService.getDailyClicks(shortedUrl);
  }
}
