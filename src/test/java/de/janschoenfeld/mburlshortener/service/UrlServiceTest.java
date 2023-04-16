package de.janschoenfeld.mburlshortener.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
class UrlServiceTest {

  String base62regex = "^[a-zA-Z0-9]+$";
  @Mock
  private UrlRepository repo;
  @InjectMocks
  private UrlService urlService;

  @Test
  void shouldShortenToRandom() {
    var url = "http://exa.mp/le";
    var characterLimit = 4;
    var result = urlService.shortenUrl(url, null, 4);

    assertEquals(characterLimit, result.length());
    assertTrue(result.matches(base62regex));
  }

  @Test
  void shouldShortenToRandom_collisionDetected() {
    var url = "http://exa.mp/le";
    var characterLimit = 4;

    when(repo.findByShorted(any())).thenReturn(Optional.of(new Url()));

    var result = urlService.shortenUrl(url, null, 4);

    assertEquals(characterLimit - 1, result.length());
    assertTrue(result.matches(base62regex));
  }

  @Test
  void shouldShortenToTarget() {
    var url = "http://exa.mp/le";
    var target = "ziel";
    var characterLimit = 4;

    var result = urlService.shortenUrl(url, target, characterLimit);

    assertEquals(result, target);
  }

  @Test
  void shouldThrow400_onTargetTaken() {
    var url = "http://exa.mp/le";
    var target = "ziel";
    var characterLimit = 4;

    when(repo.findByShorted(target)).thenReturn(Optional.of(new Url()));

    var exception =
        assertThrows(ResponseStatusException.class, () -> urlService.shortenUrl(url, target, characterLimit));

    assertEquals(400, exception.getBody().getStatus());
  }

  @Test
  void shouldReturnShortenedUrl_IfUrlExists() {
    var url = "http://exa.mp/le";
    var characterLimit = 4;

    final var existingShortenedUrl = "iExist";
    when(repo.findFirstByOriginal(url)).thenReturn(Optional.of(new Url(url, existingShortenedUrl)));

    var result = urlService.shortenUrl(url, null, characterLimit);

    assertEquals(existingShortenedUrl, result);
  }
}