package de.janschoenfeld.mburlshortener.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
@Entity
@Table
public class Url {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String original;
  private String shorted;
  private Long createdAt = Instant.now().getEpochSecond();
  private Long timesCalled_day = 0L;

  public Url() {
  }

  public Url(String original, String shorted) {
    this.original = original;
    this.shorted = shorted;
  }
}
