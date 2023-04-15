package de.janschoenfeld.mburlshortener.controller;

import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import de.janschoenfeld.mburlshortener.model.Url;
import de.janschoenfeld.mburlshortener.model.repository.UrlRepository;
import de.janschoenfeld.mburlshortener.service.UrlService;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UrlController {

  private final UrlValidator urlValidator;
  private final UrlService urlService;
  private final UrlRepository urlRepository;

  private final int characterLimit;

  public UrlController(UrlService urlService, UrlRepository urlRepository, @Value("${validation.character-limit:6}") int characterLimit) {
    this.urlService = urlService;
    this.urlRepository = urlRepository;
    this.characterLimit = characterLimit;
    this.urlValidator = new UrlValidator();
  }

  @GetMapping("url/shorten/")
  public String shortenUrl(String url, @Nullable String target) {
    target = deleteWhitespace(target);
    validateInput(url, target);
    return urlService.shortenUrl(url, target, characterLimit);
  }

  @GetMapping({"/{shortUrl}"})
  public void getOriginalUrl(HttpServletResponse response, @PathVariable String shortUrl) {
    final var urlOptional = urlRepository.findByShorted(shortUrl);
    if (urlOptional.isPresent()) {
      try {
        final var url = urlOptional.get();
        incrementCounter(url);
        response.sendRedirect(url.getOriginal());
      } catch (IOException e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            "Error occurred while attempting to redirect the request");
      }
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No original url could be found.");
    }
  }

  private void incrementCounter(Url url) {
    url.setTimesCalled_day(url.getTimesCalled_day() + 1);
    urlRepository.save(url);
  }

  private void validateInput(String url, String target) {
    if (!urlValidator.isValid(url)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(String.format("'%s' is not a valid url", url)));
    }
    if (!isEmpty(target) && target.length() > characterLimit) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("Target URL cannot exceed %s characters", characterLimit));
    }
  }
}
