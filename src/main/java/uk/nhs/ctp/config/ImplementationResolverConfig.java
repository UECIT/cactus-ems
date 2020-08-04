package uk.nhs.ctp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.ctp.transform.ReferralRequestDTOTransformer;
import uk.nhs.ctp.transform.one_one.ReferralRequestDTOOneOneTransformer;
import uk.nhs.ctp.transform.two.ReferralRequestDTOTwoTransformer;
import uk.nhs.ctp.utils.ImplementationResolver;

@Configuration
public class ImplementationResolverConfig {

  @Autowired
  private ReferralRequestDTOOneOneTransformer referralRequestVOneOneTransformer;
  @Autowired
  private ReferralRequestDTOTwoTransformer referralRequestVTwoTransformer;

  @Bean
  public ImplementationResolver<ReferralRequestDTOTransformer> referralRequestTransformer() {
    return ImplementationResolver.<ReferralRequestDTOTransformer>builder()
        .v1Impl(referralRequestVOneOneTransformer)
        .v2Impl(referralRequestVTwoTransformer)
        .build();
  }

}
