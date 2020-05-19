package uk.nhs.ctp.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.entities.UserEntity;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CdssSupplierRepository;
import uk.nhs.ctp.repos.UserRepository;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;

@RunWith(MockitoJUnitRunner.class)
public class CdssSupplierServiceTest {

  @InjectMocks
  private CdssSupplierService cdssSupplierService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CdssSupplierRepository cdssSupplierRepository;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testAllSuppliersReturnedWhenRoleIsNhsUser() {
    String username = "nhs";
    UserEntity nhsUser = new UserEntity();
    nhsUser.setUsername(username);
    nhsUser.setRole("ROLE_NHS");
    CdssSupplier input = new CdssSupplier();
    input.setName("test");
    when(userRepository.findByUsername(username)).thenReturn(nhsUser);
    when(cdssSupplierRepository.findAllBySupplierId(null))
        .thenReturn(Collections.singletonList(input));

    List<CdssSupplierDTO> suppliers = cdssSupplierService.getCdssSuppliers("nhs");

    CdssSupplierDTO expected = new CdssSupplierDTO();
    expected.setName("test");
    assertThat(suppliers, contains(samePropertyValuesAs(expected)));

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
    when(cdssSupplierRepository.findAllBySupplierId(null))
        .thenReturn(Collections.singletonList(input));

    List<CdssSupplierDTO> suppliers = cdssSupplierService.getCdssSuppliers("admin");

    CdssSupplierDTO expected = new CdssSupplierDTO();
    expected.setName("test");
    assertThat(suppliers, contains(samePropertyValuesAs(expected)));
  }

  @Test
  public void testSpecificSuppliersRetrievedWhenRoleIsNotAdminOrNhsUser() {
    String username = "cdss";
    UserEntity nhsUser = new UserEntity();
    nhsUser.setUsername(username);
    nhsUser.setRole("ROLE_CDSS");
    CdssSupplier input = new CdssSupplier();
    input.setName("test");
    nhsUser.setCdssSuppliers(Collections.singletonList(input));
    when(userRepository.findByUsername(username)).thenReturn(nhsUser);

    List<CdssSupplierDTO> suppliers = cdssSupplierService.getCdssSuppliers("cdss");

    CdssSupplierDTO expected = new CdssSupplierDTO();
    expected.setName("test");
    assertThat(suppliers, contains(samePropertyValuesAs(expected)));
    verify(cdssSupplierRepository, never()).findAllBySupplierId(any());
  }

  @Test
  public void testExceptionThrownWhenUserHasInvalidRole() {
    UserEntity invalid = new UserEntity();
    invalid.setRole("NOT_A_ROLE");
    when(userRepository.findByUsername(anyString())).thenReturn(invalid);

    expectedException.expect(EMSException.class);
    cdssSupplierService.getCdssSuppliers("anything");

  }

}
