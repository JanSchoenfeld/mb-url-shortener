package de.janschoenfeld.mburlshortener.controller;

import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import de.janschoenfeld.mburlshortener.model.repository.UrlRepository;
import de.janschoenfeld.mburlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class UrlController {

  private final UrlValidator urlValidator;
  private final UrlService urlService;
  private final UrlRepository urlRepository;

  private final int characterLimit;

  public UrlController(UrlService urlService, UrlRepository urlRepository,
                       @Value("${validation.character-limit:6}") int characterLimit) {
    this.urlService = urlService;
    this.urlRepository = urlRepository;
    this.characterLimit = characterLimit;
    this.urlValidator = new UrlValidator();
  }

  @PostMapping("url/shorten/")
  @Operation(summary = "Takes an input URL and shortens it. "
                       + "If a target URL is provided the server will try to target as custom URL")
  public String shortenUrl(String url, @RequestParam(required = false) String target) {
    target = deleteWhitespace(target);
    validateInput(url, target);
    return buildResponseUrl(urlService.shortenUrl(url, target, characterLimit));
  }

  @GetMapping({"/{shortUrl}"})
  @Operation(summary = "Takes a shorted URL as input and redirects to the original URL")
  public void getOriginalUrl(HttpServletResponse response, @PathVariable String shortUrl) {
    final var urlOptional = urlRepository.findByShorted(shortUrl);
    if (urlOptional.isPresent()) {
      try {
        final var url = urlOptional.get();
        urlService.incrementCounter(url);
        response.sendRedirect(url.getOriginal());
      } catch (IOException e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            "Error occurred while attempting to redirect the request");
      }
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No original url could be found");
    }
  }

  private void validateInput(String url, String target) {
    if (!urlValidator.isValid(url)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(String.format("'%s' is not a valid url", url)));
    }
    if (validateTarget(target)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("Target URL contains more than %s or invalid characters", characterLimit));
    }
  }

  private String buildResponseUrl(String shorted) {
    UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath().path(shorted + "/");
    return builder.build().toUriString();
  }

  private boolean validateTarget(String target) {
    return !isEmpty(target) && target.length() > characterLimit && target.matches("^[a-zA-Z0-9]+$");
  }
}
