package uk.nhs.ctp.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.nhs.ctp.enums.CdsApiVersion.ONE_ONE;
import static uk.nhs.ctp.enums.CdsApiVersion.TWO;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.nhs.ctp.model.Appointment;
import uk.nhs.ctp.model.SupplierAccountDetails;

public class ImplementationResolverTest {

  private ImplementationResolver<Object> resolver;

  @Rule
  public ExpectedException expect = ExpectedException.none();

  @Test
  public void shouldFail_noCdsApiVersionSet() {
    resolver = ImplementationResolver.builder()
        .v1Impl("v1String")
        .v2Impl("v2String")
        .build();
    expect.expectMessage("No api version set");
    expect.expect(NullPointerException.class);

    resolver.resolve(null);
  }

  @Test
  public void returnsVersionOnePointOneImplementation() {
    Appointment v1 = Appointment.builder().comment("v1 Comment").build();
    Appointment v2 = Appointment.builder().comment("v2 Comment").build();
    resolver = ImplementationResolver.builder()
        .v1Impl(v1)
        .v2Impl(v2)
        .build();

    Object resolved = resolver.resolve(ONE_ONE);

    assertThat(resolved, is(v1));
  }

  @Test
  public void returnsVersionTwoImplementation() {
    SupplierAccountDetails v1 = SupplierAccountDetails.builder().email("v1 Email").build();
    SupplierAccountDetails v2 = SupplierAccountDetails.builder().email("v2 Email").build();
    resolver = ImplementationResolver.builder()
        .v1Impl(v1)
        .v2Impl(v2)
        .build();

    Object resolved = resolver.resolve(TWO);

    assertThat(resolved, is(v2));
  }

}