package uk.nhs.ctp.utils;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.enums.CdsApiVersion;

@Component
public class ImplementationResolver {

  public <T, R extends T> T resolve(CdsApiVersion apiVersion, R v1Impl, R v2Impl) {
    Preconditions.checkNotNull(apiVersion);
    switch (apiVersion) {
      case ONE_ONE:
        return v1Impl;
      case TWO:
        return v2Impl;
    }
    throw new IllegalStateException("Api version " + apiVersion + " not supported");
  }

}
