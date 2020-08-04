package uk.nhs.ctp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.security.SupplierTokenResolver;

@CrossOrigin
@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class AuthController {

  private final SupplierTokenResolver supplierTokenResolver;

  @GetMapping(path = "/exchange")
  public String exchangeToken(@RequestParam String baseUrl) {
    return supplierTokenResolver.resolve(baseUrl)
        .orElseThrow(EMSException::notFound);
  }

}
