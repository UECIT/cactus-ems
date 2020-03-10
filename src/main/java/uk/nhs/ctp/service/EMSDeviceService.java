package uk.nhs.ctp.service;

import org.hl7.fhir.dstu3.model.Device;
import org.hl7.fhir.dstu3.model.Device.DeviceUdiComponent;
import org.hl7.fhir.dstu3.model.Device.FHIRDeviceStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.enums.DeviceKind;

@Service
public class EMSDeviceService {

  public static final String MAIN_ID = "ems-test-harness-device";

  @Value("${ems.fhir.server}")
  private String emsServer;

  public Device getEms() {
    Device ems = new Device();
    ems.setId(MAIN_ID);

    return ems
        .setStatus(FHIRDeviceStatus.ACTIVE)
        .setType(DeviceKind.APPLICATION_SOFTWARE.toCodeableConcept())
        .setUdi(new DeviceUdiComponent()
            .setName("EMS Test Harness"));
  }
}
