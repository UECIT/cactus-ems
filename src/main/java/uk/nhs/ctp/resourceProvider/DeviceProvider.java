package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.hl7.fhir.dstu3.model.Device;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.EMSDeviceService;

@Component
@RequiredArgsConstructor
public class DeviceProvider implements IResourceProvider {

  private final EMSDeviceService emsDeviceService;

  @Read
  public Device getEmsDevice(@IdParam IdType id) {
    if (ObjectUtils.notEqual(id.getIdPart(), EMSDeviceService.MAIN_ID)) {
      throw new ResourceNotFoundException(id);
    }

    return emsDeviceService.getEms();
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Device.class;
  }
}
