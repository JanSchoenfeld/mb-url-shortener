package de.janschoenfeld.mburlshortener.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
  private String shortened;
  private Long timesCalledToday = 0L;

}
