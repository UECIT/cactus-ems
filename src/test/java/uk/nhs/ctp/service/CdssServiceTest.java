package uk.nhs.ctp.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.repos.CdssSupplierRepository;

@RunWith(MockitoJUnitRunner.class)
public class CdssServiceTest {

  @InjectMocks
  private CdssService cdssService;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private CdssSupplierRepository cdssSupplierRepository;

  @Mock
  private TokenAuthenticationService authenticationService;

  private static final String FAKE_SUPPLIER = "somesupplier";

  @Before
  public void setup() {
    when(authenticationService.requireSupplierId()).thenReturn(FAKE_SUPPLIER);
  }

  @Test
  public void shouldFetchImageFromCdss() {
    Long cdssId = 4L;
    CdssSupplier supplier = new CdssSupplier();
    supplier.setBaseUrl("cdss/url/fhir");

    when(cdssSupplierRepository.getOneByIdAndSupplierId(cdssId, FAKE_SUPPLIER))
        .thenReturn(Optional.of(supplier));
    when(restTemplate.getForObject("cdss/url/image/some.image.png", byte[].class))
        .thenReturn("encodedImage".getBytes());

    byte[] image = cdssService.getImage(cdssId, "some.image.png");
    assertThat(image, is("encodedImage".getBytes()));
  }

}