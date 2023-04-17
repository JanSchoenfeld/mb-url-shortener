package de.janschoenfeld.mburlshortener.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Data;

@Data
@Entity
@Table
public class Url {

  @Id
  private String shorted;
  private String original;
  private Long createdAt = Instant.now().getEpochSecond();
  private Long timesCalled_day = 0L;

  public Url() {
  }

  public Url(String original, String shorted) {
    this.original = original;
    this.shorted = shorted;
  }
}
