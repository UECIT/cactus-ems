package uk.nhs.ctp.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "entity_id")
public class EntityId {

  public static final Long INITIAL_VALUE = 1L;

  @Id
  private String name;
  private long value;

  public EntityId(String name) {
    this.value = INITIAL_VALUE;
    this.name = name;
  }

}
