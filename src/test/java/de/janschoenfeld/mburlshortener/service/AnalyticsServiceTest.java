package de.janschoenfeld.mburlshortener.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import de.janschoenfeld.mburlshortener.model.Url;
import de.janschoenfeld.mburlshortener.model.repository.UrlRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

  @Mock
  private UrlRepository repo;

  @InjectMocks
  private AnalyticsService analyticsService;

  @Test
  void shouldReturnCorrectDailyClicks() {
    var shorted = "test";
    var entity = new Url();
    var expected = 15L;
    entity.setTimesCalled_day(expected);
    when(repo.findByShorted(shorted)).thenReturn(Optional.of(entity));

    var result = analyticsService.getDailyClicks(shorted);
    assertEquals(expected, result);
  }

  @Test
  void shouldReturn404_forNoResult() {
    var shorted = "test";
    when(repo.findByShorted(shorted)).thenReturn(Optional.empty());

    var exception = assertThrows(ResponseStatusException.class, () -> analyticsService.getDailyClicks(shorted));

    assertEquals(404, exception.getBody().getStatus());
  }
}