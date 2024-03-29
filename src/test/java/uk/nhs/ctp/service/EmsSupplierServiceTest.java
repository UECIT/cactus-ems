package uk.nhs.ctp.service;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.entities.EmsSupplier;
import uk.nhs.ctp.repos.EmsSupplierRepository;

@RunWith(MockitoJUnitRunner.class)
public class EmsSupplierServiceTest {

  private static final String SUPPLIER = "supplier";

  @InjectMocks
  private EmsSupplierService emsSupplierService;

  @Mock
  private EmsSupplierRepository emsSupplierRepository;
  @Mock
  private TokenAuthenticationService authService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setup() {
    when(authService.requireSupplierId()).thenReturn(SUPPLIER);
  }

  @Test
  public void testFindCdssSupplierByUrl_matches() {
    EmsSupplier matched = new EmsSupplier();
    matched.setBaseUrl("matched.base");
    when(emsSupplierRepository.getOneBySupplierIdAndBaseUrl(SUPPLIER, "matched.base"))
        .thenReturn(Optional.of(matched));

    Optional<EmsSupplier> found = emsSupplierService
        .findEmsSupplierByBaseUrl("matched.base");

    assertThat(found, isPresentAndIs(matched));
  }

  @Test
  public void testFindCdssSupplierByUrl_noMatches() {
    when(emsSupplierRepository.getOneBySupplierIdAndBaseUrl(SUPPLIER, "matched.base"))
        .thenReturn(Optional.empty());

    Optional<EmsSupplier> found = emsSupplierService
        .findEmsSupplierByBaseUrl("matched.base");

    assertThat(found, isEmpty());
  }
}