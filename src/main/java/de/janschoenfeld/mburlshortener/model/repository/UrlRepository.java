package de.janschoenfeld.mburlshortener.model.repository;

import de.janschoenfeld.mburlshortener.model.Url;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UrlRepository extends CrudRepository<Url, UUID> {

  Optional<Url> findByShorted(String shorted);

}
