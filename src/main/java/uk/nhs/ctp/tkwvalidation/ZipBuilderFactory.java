package uk.nhs.ctp.tkwvalidation;

import org.springframework.stereotype.Component;

@Component
public class ZipBuilderFactory {

  public ZipBuilder create() {
    return new ZipBuilder();
  }
}
