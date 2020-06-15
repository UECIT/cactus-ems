package uk.nhs.ctp.security;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.cactus.common.security.CactusToken;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.entities.EmsSupplier;
import uk.nhs.ctp.service.CdssSupplierService;
import uk.nhs.ctp.service.EmsSupplierService;

@RunWith(MockitoJUnitRunner.class)
public class SupplierTokenResolverTest {

  @InjectMocks
  private SupplierTokenResolver tokenResolver;

  @Mock
  private CdssSupplierService cdssSupplierService;
  @Mock
  private EmsSupplierService emsSupplierService;
  @Mock
  private SecurityContext securityContext;
  @Mock
  private Authentication authentication;

  @Before
  public void setup() {
    ReflectionTestUtils.setField(tokenResolver, "fhirServer", "fhir.server");
    ReflectionTestUtils.setField(tokenResolver, "blobServer", "blob.server");
    ReflectionTestUtils.setField(tokenResolver, "emsFhirServer", "ems.fhir.server");
    ReflectionTestUtils.setField(tokenResolver, "cactusCdss", "cdss.server");
    ReflectionTestUtils.setField(tokenResolver, "dosServer", "dos.server");
  }

  @Test
  public void resolveToken_UrlMatchesCactusFhirServer() {
    resolveCactusTest("fhir.server");
  }

  @Test
  public void resolveToken_UrlMatchesCactusBlobServer() {
    resolveCactusTest("blob.server");
  }

  @Test
  public void resolveToken_UrlMatchesCactusEmsFhirServer() {
    resolveCactusTest("ems.fhir.server");
  }

  @Test
  public void resolveToken_UrlMatchesCactusDosServer() {
    resolveCactusTest("dos.server");
  }

  private void resolveCactusTest(String url) {
    CactusToken cactusToken = new CactusToken("fake.token", null);
    when(authentication.getCredentials()).thenReturn(cactusToken);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    Optional<String> resolved = tokenResolver.resolve(url);
    assertThat(resolved, isPresentAndIs("fake.token"));
    verifyZeroInteractions(cdssSupplierService, emsSupplierService);
  }

  @Test
  public void resolveToken_UrlMatchesCdssSupplier() {
    CdssSupplier supplier = new CdssSupplier();
    supplier.setBaseUrl("cdss.url");
    supplier.setAuthToken("cdss.auth.token");
    when(cdssSupplierService.findCdssSupplierByBaseUrl("cdss.url"))
        .thenReturn(Optional.of(supplier));

    Optional<String> resolved = tokenResolver.resolve("cdss.url");

    assertThat(resolved, isPresentAndIs("cdss.auth.token"));
    verifyZeroInteractions(emsSupplierService, securityContext);
  }

  @Test
  public void resolveToken_UrlMatchesEmsSupplier() {
    EmsSupplier supplier = new EmsSupplier();
    supplier.setBaseUrl("ems.url");
    supplier.setAuthToken("ems.auth.token");

    when(cdssSupplierService.findCdssSupplierByBaseUrl(any()))
        .thenReturn(Optional.empty());
    when(emsSupplierService.findEmsSupplierByBaseUrl("ems.url"))
        .thenReturn(Optional.of(supplier));

    Optional<String> resolved = tokenResolver.resolve("ems.url");

    assertThat(resolved, isPresentAndIs("ems.auth.token"));
    verifyZeroInteractions( securityContext);
  }

  @Test
  public void resolveToken_noMatch() {

    when(cdssSupplierService.findCdssSupplierByBaseUrl(any()))
        .thenReturn(Optional.empty());
    when(emsSupplierService.findEmsSupplierByBaseUrl(any()))
        .thenReturn(Optional.empty());

    Optional<String> resolved = tokenResolver.resolve("not.known");

    assertThat(resolved, isEmpty());
    verifyZeroInteractions( securityContext);
  }
}