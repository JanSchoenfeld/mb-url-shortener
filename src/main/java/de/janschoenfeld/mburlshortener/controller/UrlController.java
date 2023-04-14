package de.janschoenfeld.mburlshortener.controller;

import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import de.janschoenfeld.mburlshortener.model.Url;
import de.janschoenfeld.mburlshortener.model.repository.UrlRepository;
import de.janschoenfeld.mburlshortener.service.UrlService;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class UrlController {

  private final UrlValidator urlValidator;
  private final UrlService urlService;
  private final UrlRepository urlRepository;

  @Value("${validation.character-limit:6}")
  private final int CHARACTER_LIMIT;

  @GetMapping("url/shorten/")
  public String shortenUrl(String url, String target) {
    validateInput(url, target);
    return urlService.shortenUrl(url, target);
  }

  @GetMapping({"url"})
  public ResponseEntity<Void> getOriginalUrl(@PathVariable String shortUrl){
    final var urlOptional = urlRepository.findByShorted(shortUrl);
    if (urlOptional.isPresent()) {
      return ResponseEntity.status(HttpStatus.FOUND)
                           .location(URI.create(urlOptional.get().getOriginal()))
                           .build();
    }
    return ResponseEntity.notFound().build();
  }

  private void validateInput(String url, String target) {
    if (!urlValidator.isValid(url)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(String.format("'%s' is not a valid url", url)));
    }
    if (!isEmpty(target) && deleteWhitespace(target).length() > CHARACTER_LIMIT) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("Target URL cannot exceed %s characters", CHARACTER_LIMIT));
    }
  }
}
