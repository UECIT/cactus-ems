package uk.nhs.ctp.repos;

import java.math.BigInteger;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EntityIdIncrementerImpl implements EntityIdIncrementer {

  private EntityManager entityManager; //A bean?

  @Override
  @Transactional
  public long incrementAndGet(String name) {
    entityManager.createNativeQuery(
        "UPDATE resource_id "
            + "SET value = LAST_INSERT_ID(value + 1) "
            + "WHERE name = :name")
        .setParameter("name", name)
        .executeUpdate();

    var query = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
    return ((BigInteger) query.getSingleResult()).longValue();

  }
}
