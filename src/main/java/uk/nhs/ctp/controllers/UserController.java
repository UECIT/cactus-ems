package uk.nhs.ctp.controllers;

import java.util.List;
import java.util.UUID;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.entities.UserEntity;
import uk.nhs.ctp.model.RegisterSupplierRequest;
import uk.nhs.ctp.model.SupplierAccountDetails;
import uk.nhs.ctp.model.SupplierAccountDetails.EndpointDetails;
import uk.nhs.ctp.service.UserManagementService;
import uk.nhs.ctp.service.dto.ChangePasswordDTO;
import uk.nhs.ctp.service.dto.NewUserDTO;
import uk.nhs.ctp.service.dto.UserDTO;

@CrossOrigin
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

  private final UserManagementService userManagementService;

  @PostMapping(path = "/register")
  public @ResponseBody SupplierAccountDetails signup(@RequestBody RegisterSupplierRequest request) {
    return SupplierAccountDetails.builder()
        .jwt(UUID.randomUUID().toString())
        .username(request.getSupplierId())
        .password("a generated password")
        .endpoints(EndpointDetails.builder()
            .ems("here")
            .cdss("there")
            .dos("everywhere")
            .build())
        .build();
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping
  public @ResponseBody
  List<UserDTO> getUsers() {
    return userManagementService.getAllUsers();
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_NHS', 'ROLE_CDSS')")
  @GetMapping(path = "/{username}")
  public @ResponseBody
  UserDTO getUserByUsername(@PathVariable("username") String username) throws NotFoundException {
    return userManagementService.getUserByUsername(username);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping
  public @ResponseBody
  UserEntity updateUser(@RequestBody UserDTO user) throws Exception {
    return userManagementService.updateUser(user);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping(path = "/reset")
  public @ResponseBody
  UserEntity resetPassword(@RequestBody ChangePasswordDTO changePassword) throws Exception {
    return userManagementService.resetPassword(changePassword);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @DeleteMapping(path = "/{username}")
  public @ResponseBody
  void deleteUser(@PathVariable("username") String username) throws Exception {
    userManagementService.deleteUser(username);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping
  public @ResponseBody
  UserEntity createUser(@RequestBody NewUserDTO user) throws Exception {
    return userManagementService.createUser(user);
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_NHS','ROLE_CDSS')")
  @PutMapping(path = "/update")
  public @ResponseBody
  UserEntity updatePassword(@RequestBody ChangePasswordDTO changePassword) throws Exception {
    return userManagementService.updatePassword(changePassword);
  }
}
