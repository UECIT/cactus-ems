package uk.nhs.ctp.utils;

import com.google.common.base.Preconditions;
import lombok.Builder;
import uk.nhs.ctp.enums.CdsApiVersion;

@Builder
public class ImplementationResolver<T> {

  T v1Impl;
  T v2Impl;

  public T resolve(CdsApiVersion apiVersion) {
    Preconditions.checkNotNull(apiVersion, "No api version set");
    switch (apiVersion) {
      case ONE_ONE:
        return this.v1Impl;
      case TWO:
        return this.v2Impl;
    }
    throw new IllegalStateException("Api version " + apiVersion + " not supported");
  }

}
