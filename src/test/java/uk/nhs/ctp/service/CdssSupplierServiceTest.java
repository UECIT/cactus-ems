package uk.nhs.ctp.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.entities.UserEntity;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CdssSupplierRepository;
import uk.nhs.ctp.repos.UserRepository;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CdssSupplierServiceTest {
	
	@InjectMocks
	private CdssSupplierService spyCdssSupplierService;
	
	@Mock
	private UserRepository mockUserRepository;
	
	@Mock
	private CdssSupplierRepository mockCdssSupplierRepository;
	
	CdssSupplier cdss1, cdss2;
	List<CdssSupplier> cdssSuppliersList1, cdssSuppliersList2;
	CdssSupplierDTO cdssDTO1, cdssDTO2;
	List<CdssSupplierDTO> cdssSupplierDTOsList1, cdssSupplierDTOsList2;
	UserEntity adminUser, nhsUser, cdssUser, invalidRoleUser;
	
	@Before
	public void setup() {
		spyCdssSupplierService = spy(new CdssSupplierService());
		MockitoAnnotations.initMocks(this);
		cdss1 = mock(CdssSupplier.class);
		cdss2 = mock(CdssSupplier.class);
		adminUser = mock(UserEntity.class);
		nhsUser = mock(UserEntity.class);
		cdssUser = mock(UserEntity.class);
		invalidRoleUser = mock(UserEntity.class);
		
		cdssSuppliersList1 = new ArrayList<>();
		cdssSuppliersList2 = new ArrayList<>();
		
		cdssSuppliersList1.add(cdss1);
		cdssSuppliersList2.add(cdss1);
		cdssSuppliersList2.add(cdss2);
		
		cdssDTO1 = mock(CdssSupplierDTO.class);
		cdssDTO2 = mock(CdssSupplierDTO.class);
		
		cdssSupplierDTOsList1 = new ArrayList<>();
		cdssSupplierDTOsList2 = new ArrayList<>();
		
		cdssSupplierDTOsList1.add(cdssDTO1);
		cdssSupplierDTOsList2.add(cdssDTO1);
		cdssSupplierDTOsList2.add(cdssDTO2);
		
		when(adminUser.getRole()).thenReturn("ROLE_ADMIN");
		when(nhsUser.getRole()).thenReturn("ROLE_NHS");
		when(cdssUser.getRole()).thenReturn("ROLE_CDSS");
		when(invalidRoleUser.getRole()).thenReturn("INVALID_ROLE");
		
		when(mockUserRepository.findByUsername("admin")).thenReturn(adminUser);
		when(mockUserRepository.findByUsername("nhs")).thenReturn(nhsUser);
		when(mockUserRepository.findByUsername("cdss")).thenReturn(cdssUser);
		when(mockUserRepository.findByUsername("invalid")).thenReturn(invalidRoleUser);

		when(mockCdssSupplierRepository.findAll()).thenReturn(cdssSuppliersList2);
		when(cdssUser.getCdssSuppliers()).thenReturn(cdssSuppliersList1);
		
		doReturn(cdssSupplierDTOsList1).when(spyCdssSupplierService).convertToSupplierDTO(cdssSuppliersList1);
		doReturn(cdssSupplierDTOsList2).when(spyCdssSupplierService).convertToSupplierDTO(cdssSuppliersList2);

	}

	@Test
	public void testAllSuppliersReturnedWhenRoleIsNhsUser() {
		List<CdssSupplierDTO> result = spyCdssSupplierService.getCdssSuppliers("admin");
		
		assertTrue(result.size() == 2);
		
		verify(mockCdssSupplierRepository, times(1)).findAll();
		verify(adminUser, times(0)).getCdssSuppliers();
	}
	
	@Test
	public void testAllSuppliersReturnedWhenRoleIsAdminUser() {
		List<CdssSupplierDTO> result = spyCdssSupplierService.getCdssSuppliers("nhs");
		
		assertTrue(result.size() == 2);
		
		verify(mockCdssSupplierRepository, times(1)).findAll();
		verify(nhsUser, times(0)).getCdssSuppliers();
	}
	
	@Test
	public void testSpecificSuppliersRetrievedWhenRoleIsNotAdminOrNhsUser() {
		List<CdssSupplierDTO> result = spyCdssSupplierService.getCdssSuppliers("cdss");
		
		assertTrue(result.size() == 1);
		
		verify(mockCdssSupplierRepository, times(0)).findAll();
		verify(cdssUser, times(1)).getCdssSuppliers();
	}
	
	@Test(expected = EMSException.class)
	public void testExceptionThrownWhenUserHasInvalidRole() {
		spyCdssSupplierService.getCdssSuppliers("invalid");
	}

}
