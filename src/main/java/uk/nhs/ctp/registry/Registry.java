package uk.nhs.ctp.registry;

import java.util.List;

public interface Registry<T> {
  List<T> getAll();
}
