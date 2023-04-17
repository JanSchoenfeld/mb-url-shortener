package de.janschoenfeld.mburlshortener.model.repository;

import de.janschoenfeld.mburlshortener.model.Url;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UrlRepository extends CrudRepository<Url, String> {

  Optional<Url> findByShorted(String shorted);

  Optional<Url> findFirstByOriginal(String original);

  @Modifying
  @Transactional
  @Query(value = "UPDATE Url u SET u.timesCalled_day = 0")
  void resetDailyClicks();

}
