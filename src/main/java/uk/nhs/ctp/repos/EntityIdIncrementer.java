package uk.nhs.ctp.repos;

public interface EntityIdIncrementer {
  long incrementAndGet(String name);
}
