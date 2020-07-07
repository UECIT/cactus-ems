package uk.nhs.ctp.utils;

import org.springframework.stereotype.Component;

@Component
public class ZipBuilderFactory {

  public ZipBuilder create() {
    return new ZipBuilder();
  }
}
