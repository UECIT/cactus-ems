package uk.nhs.ctp.service;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
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
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.entities.ServiceDefinition;
import uk.nhs.ctp.entities.UserEntity;
import uk.nhs.ctp.enums.CdsApiVersion;
import uk.nhs.ctp.enums.ReferencingType;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CdssSupplierRepository;
import uk.nhs.ctp.repos.ServiceDefinitionRepository;
import uk.nhs.ctp.repos.UserRepository;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;
import uk.nhs.ctp.service.dto.NewCdssSupplierDTO;
import uk.nhs.ctp.service.dto.ServiceDefinitionDTO;
import uk.nhs.ctp.transform.CdssSupplierDTOTransformer;

@RunWith(MockitoJUnitRunner.class)
public class CdssSupplierServiceTest {

  private static final String SUPPLIER = "supplier";

  @InjectMocks
  private CdssSupplierService cdssSupplierService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CdssSupplierRepository cdssSupplierRepository;

  @Mock
  private ServiceDefinitionRepository serviceDefinitionRepository;

  @Mock
  private TokenAuthenticationService tokenAuthenticationService;

  @Mock
  private CdssSupplierDTOTransformer cdssTransformer;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setup() {
    when(tokenAuthenticationService.requireSupplierId()).thenReturn(SUPPLIER);
  }

  @Test
  public void testAllSuppliersReturnedWhenRoleIsNhsUser() {
    String username = "nhs";
    UserEntity nhsUser = new UserEntity();
    nhsUser.setUsername(username);
    nhsUser.setRole("ROLE_NHS");
    CdssSupplier input = new CdssSupplier();
    input.setName("test");
    when(userRepository.findByUsername(username)).thenReturn(nhsUser);
    when(cdssSupplierRepository.findAllBySupplierId(SUPPLIER))
        .thenReturn(Collections.singletonList(input));

    CdssSupplierDTO expected = new CdssSupplierDTO();
    expected.setName("test");
    when(cdssTransformer.transform(input))
        .thenReturn(expected);

    List<CdssSupplierDTO> suppliers = cdssSupplierService.getCdssSuppliers("nhs");

    assertThat(suppliers, contains(expected));

  }

  @Test
  public void testAllSuppliersReturnedWhenRoleIsAdminUser() {
    String username = "admin";
    UserEntity nhsUser = new UserEntity();
    nhsUser.setUsername(username);
    nhsUser.setRole("ROLE_ADMIN");
    CdssSupplier input = new CdssSupplier();
    input.setName("test");
    when(userRepository.findByUsername(username)).thenReturn(nhsUser);
    when(cdssSupplierRepository.findAllBySupplierId(SUPPLIER))
        .thenReturn(Collections.singletonList(input));

    CdssSupplierDTO expected = new CdssSupplierDTO();
    expected.setName("test");
    when(cdssTransformer.transform(input))
        .thenReturn(expected);

    List<CdssSupplierDTO> suppliers = cdssSupplierService.getCdssSuppliers("admin");

    assertThat(suppliers, contains(expected));
  }

  @Test
  public void testExceptionThrownWhenUserHasInvalidRole() {
    UserEntity invalid = new UserEntity();
    invalid.setRole("NOT_A_ROLE");
    when(userRepository.findByUsername(anyString())).thenReturn(invalid);

    expectedException.expect(EMSException.class);
    cdssSupplierService.getCdssSuppliers("anything");

  }

  @Test
  public void testCreateCdssSupplierWithServiceDefinitions() {
    ServiceDefinitionDTO sd = new ServiceDefinitionDTO();
    sd.setServiceDefinitionId("servicedef");
    sd.setDescription("description");

    NewCdssSupplierDTO request = new NewCdssSupplierDTO();
    request.setBaseUrl("http://base.com/fhir");
    request.setName("Test");
    request.setInputDataRefType(ReferencingType.BY_REFERENCE);
    request.setInputParamsRefType(ReferencingType.BY_RESOURCE);
    request.setServiceDefinitions(Collections.singletonList(sd));
    request.setSupportedVersion(CdsApiVersion.ONE_ONE);
    CdssSupplier expected = new CdssSupplier();
    expected.setId(99L);
    expected.setName("Test");
    expected.setInputDataRefType(ReferencingType.BY_REFERENCE);
    expected.setInputParamsRefType(ReferencingType.BY_RESOURCE);
    expected.setBaseUrl("http://base.com/fhir");
    expected.setSupportedVersion(CdsApiVersion.ONE_ONE);

    when(cdssSupplierRepository.save(any(CdssSupplier.class)))
        .then(invocationOnMock -> {
          CdssSupplier supplier = invocationOnMock.getArgumentAt(0, CdssSupplier.class);
          supplier.setId(99L);
          return supplier;
        });
    when(cdssSupplierRepository.getOne(99L)).thenReturn(expected);

    CdssSupplier returned = cdssSupplierService.createCdssSupplier(request);

    assertThat(returned, is(expected));

    ServiceDefinition expectedSd = new ServiceDefinition();
    expectedSd.setCdssSupplierId(99L);
    expectedSd.setServiceDefinitionId("servicedef");
    expectedSd.setDescription("description");
    expectedSd.setSupplierId(SUPPLIER);
    verify(serviceDefinitionRepository).save(argThat(samePropertyValuesAs(expectedSd)));
  }

  @Test
  public void testCreateCdssSupplierNoServiceDefinitions() {
    NewCdssSupplierDTO request = new NewCdssSupplierDTO();
    request.setBaseUrl("http://base.com/fhir");
    request.setName("Test");
    request.setInputDataRefType(ReferencingType.BY_REFERENCE);
    request.setInputParamsRefType(ReferencingType.BY_RESOURCE);
    request.setSupportedVersion(CdsApiVersion.TWO);
    CdssSupplier expected = new CdssSupplier();
    expected.setId(99L);
    expected.setName("Test");
    expected.setInputDataRefType(ReferencingType.BY_REFERENCE);
    expected.setInputParamsRefType(ReferencingType.BY_RESOURCE);
    expected.setBaseUrl("http://base.com/fhir");
    expected.setSupportedVersion(CdsApiVersion.TWO);

    when(cdssSupplierRepository.save(any(CdssSupplier.class)))
        .then(invocationOnMock -> {
          CdssSupplier supplier = invocationOnMock.getArgumentAt(0, CdssSupplier.class);
          supplier.setId(99L);
          return supplier;
        });
    when(cdssSupplierRepository.getOne(99L)).thenReturn(expected);

    CdssSupplier returned = cdssSupplierService.createCdssSupplier(request);

    assertThat(returned, is(expected));
    verify(serviceDefinitionRepository, never()).save(any(ServiceDefinition.class));

  }

  @Test
  public void testFindCdssSupplierByUrl_matches() {
    CdssSupplier matched = new CdssSupplier();
    matched.setBaseUrl("matched.base");
    when(cdssSupplierRepository.getOneBySupplierIdAndBaseUrl(SUPPLIER, "matched.base"))
        .thenReturn(Optional.of(matched));

    Optional<CdssSupplier> found = cdssSupplierService
        .findCdssSupplierByBaseUrl("matched.base");

    assertThat(found, isPresentAndIs(matched));
  }

  @Test
  public void testFindCdssSupplierByUrl_noMatches() {
    when(cdssSupplierRepository.getOneBySupplierIdAndBaseUrl(SUPPLIER, "matched.base"))
        .thenReturn(Optional.empty());

    Optional<CdssSupplier> found = cdssSupplierService
        .findCdssSupplierByBaseUrl("matched.base");

    assertThat(found, isEmpty());
  }

}
