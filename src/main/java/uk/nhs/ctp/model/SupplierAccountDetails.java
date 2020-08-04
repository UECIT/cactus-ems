package uk.nhs.ctp.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SupplierAccountDetails {

  String username;
  String email;
  String password;
  String jwt;
  EndpointDetails endpoints;

  @Value
  @Builder
  public static class EndpointDetails {
    String cdss;
    String cdss2;
    String ems;
    String emsUi;
    String fhirServer;
    String blobServer;
    String dos;
    String logs;
  }

}
