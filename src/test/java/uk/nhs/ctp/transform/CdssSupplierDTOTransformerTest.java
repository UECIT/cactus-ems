package uk.nhs.ctp.transform;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.entities.ServiceDefinition;
import uk.nhs.ctp.enums.CdsApiVersion;
import uk.nhs.ctp.enums.ReferencingType;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;
import uk.nhs.ctp.service.dto.ServiceDefinitionDTO;

public class CdssSupplierDTOTransformerTest {

  private CdssSupplierDTOTransformer transformer;

  @Before
  public void setup() {
    transformer = new CdssSupplierDTOTransformer();
  }

  @Test
  public void transformSupplierWithServiceDefinitions() {
    CdssSupplier supplier = new CdssSupplier();
    supplier.setInputDataRefType(ReferencingType.BY_REFERENCE);
    supplier.setInputParamsRefType(ReferencingType.BY_RESOURCE);
    supplier.setName("supplier name");
    supplier.setBaseUrl("base.url.com");
    supplier.setId(4L);
    supplier.setSupportedVersion(CdsApiVersion.ONE_ONE);

    CdssSupplierDTO result = transformer.transform(supplier);

    CdssSupplierDTO expected = new CdssSupplierDTO();
    expected.setInputDataRefType(ReferencingType.BY_REFERENCE);
    expected.setInputParamsRefType(ReferencingType.BY_RESOURCE);
    expected.setName("supplier name");
    expected.setBaseUrl("base.url.com");
    expected.setId(4L);
    expected.setSupportedVersion(CdsApiVersion.ONE_ONE);
    assertThat(result, sameBeanAs(expected));
  }

  @Test
  public void transformSupplierWithoutServiceDefinitions() {
    ServiceDefinition serviceDefinition = new ServiceDefinition();
    serviceDefinition.setCdssSupplierId(4L);
    serviceDefinition.setDescription("description");
    serviceDefinition.setServiceDefinitionId("sdid");
    serviceDefinition.setId(7L);
    CdssSupplier supplier = new CdssSupplier();
    supplier.setInputDataRefType(ReferencingType.BY_RESOURCE);
    supplier.setInputParamsRefType(ReferencingType.BY_REFERENCE);
    supplier.setName("supplier name");
    supplier.setBaseUrl("base.url.com");
    supplier.setId(4L);
    supplier.setSupportedVersion(CdsApiVersion.TWO);
    supplier.setServiceDefinitions(Collections.singletonList(serviceDefinition));

    CdssSupplierDTO result = transformer.transform(supplier);

    ServiceDefinitionDTO expectedSd = new ServiceDefinitionDTO();
    expectedSd.setServiceDefinitionId("sdid");
    expectedSd.setDescription("description");
    CdssSupplierDTO expected = new CdssSupplierDTO();
    expected.setInputDataRefType(ReferencingType.BY_RESOURCE);
    expected.setInputParamsRefType(ReferencingType.BY_REFERENCE);
    expected.setName("supplier name");
    expected.setBaseUrl("base.url.com");
    expected.setId(4L);
    expected.setSupportedVersion(CdsApiVersion.TWO);
    expected.setServiceDefinitions(Collections.singletonList(expectedSd));
    assertThat(result, sameBeanAs(expected));
  }

}