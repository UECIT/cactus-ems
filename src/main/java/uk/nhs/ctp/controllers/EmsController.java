package uk.nhs.ctp.controllers;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.entities.EmsSupplier;
import uk.nhs.ctp.service.EmsSupplierService;

@CrossOrigin
@RestController
@RequestMapping(path = "/ems")
@RequiredArgsConstructor
public class EmsController {

  private final EmsSupplierService emsSupplierService;

  @GetMapping
  public @ResponseBody List<EmsSupplier> getEmsSuppliers() {
    return emsSupplierService.getAll();
  }

  @PostMapping
  public @ResponseBody EmsSupplier createorUpdateEMS(@RequestBody EmsSupplier emsSupplier) {
    return emsSupplierService.crupdate(emsSupplier);
  }

  @DeleteMapping(path = "/{id}")
  public @ResponseBody void deleteEmsSupplier(@PathVariable("id") Long id) {
    emsSupplierService.delete(id);
  }
}
