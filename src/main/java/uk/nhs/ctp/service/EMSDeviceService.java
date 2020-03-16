package uk.nhs.ctp.service;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Device;
import org.hl7.fhir.dstu3.model.Device.DeviceUdiComponent;
import org.hl7.fhir.dstu3.model.Device.FHIRDeviceStatus;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.enums.DeviceKind;
import uk.nhs.ctp.service.fhir.ReferenceService;

@Service
@RequiredArgsConstructor
public class EMSDeviceService {

  public static final String MAIN_ID = "ems-test-harness-device";

  private final ReferenceService referenceService;
  private final NarrativeService narrativeService;

  public Device getEms() {
    Device ems = new Device();
    ems.setId(referenceService.buildId(ResourceType.Device, MAIN_ID));
    ems.setText(narrativeService.buildNarrative(
        "The active software application of the EMS Test Harness"));

    return ems
        .setStatus(FHIRDeviceStatus.ACTIVE)
        .setType(DeviceKind.APPLICATION_SOFTWARE.toCodeableConcept())
        .setUdi(new DeviceUdiComponent().setName("EMS Test Harness"));


  }

}
