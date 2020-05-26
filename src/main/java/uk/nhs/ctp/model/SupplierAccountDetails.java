package uk.nhs.ctp.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SupplierAccountDetails {

  String username;
  String password;
  String jwt;
  EndpointDetails endpoints;


  @Value
  @Builder
  public static class EndpointDetails {
    String cdss;
    String ems;
    String dos;
  }

}
