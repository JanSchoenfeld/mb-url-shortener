package de.janschoenfeld.mburlshortener.service;

import de.janschoenfeld.mburlshortener.model.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

  private final UrlRepository urlRepository;

  public Long getDailyClicks(String shorted) {
    var urlOptional = urlRepository.findByShorted(shorted);

    if (urlOptional.isPresent()) {
      return urlOptional.get().getTimesCalled_day();
    }

    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Please provide an existing shortened Url");
  }
}

