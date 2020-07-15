package uk.nhs.ctp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.JWTHandler;
import uk.nhs.cactus.common.security.JWTRequest;
import uk.nhs.ctp.auditFinder.role.RoleMapper;
import uk.nhs.ctp.entities.UserEntity;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.model.RegisterSupplierRequest;
import uk.nhs.ctp.model.SupplierAccountDetails;
import uk.nhs.ctp.model.SupplierAccountDetails.EndpointDetails;
import uk.nhs.ctp.repos.UserRepository;
import uk.nhs.ctp.security.CognitoService;
import uk.nhs.ctp.service.dto.ChangePasswordDTO;
import uk.nhs.ctp.service.dto.NewUserDTO;
import uk.nhs.ctp.service.dto.UserDTO;
import uk.nhs.ctp.utils.ErrorHandlingUtils;
import uk.nhs.ctp.utils.PasswordUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CognitoService cognitoService;
  private final RoleMapper roleMapper;
  private final JWTHandler jwtHandler;

  @Value("${ems.fhir.server}")
  private String ems;

  @Value("${ems.frontend}")
  private String emsUi;

  @Value("${cactus.cdss}")
  private String cdss;

  @Value("${cactus.cdss.v2}")
  private String cdss2;

  @Value("${fhir.server}")
  private String fhirServer;

  @Value("${blob.server}")
  private String blobServer;

  @Value("${dos.server}")
  private String dos;

  @Value("${logs.server}")
  private String logs;

  public SupplierAccountDetails createNewSupplierUser(RegisterSupplierRequest request) {
    final String role = "ROLE_SUPPLIER_ADMIN";
    String supplierId = request.getSupplierId();
    String username = "admin_" + supplierId;

    var userDetails = new NewUserDTO();
    userDetails.setUsername(username);
    userDetails.setPassword(PasswordUtil.getStrongPassword());
    userDetails.setEnabled(true);
    userDetails.setName("<Change me>");
    userDetails.setSupplierId(supplierId);
    userDetails.setRole(role);

    try {
      createUser(userDetails);

      SupplierAccountDetails supplierAccountDetails = SupplierAccountDetails.builder()
          .jwt(jwtHandler.generate(JWTRequest.builder()
              .username(username)
              .supplierId(supplierId)
              .role(role)
              .build()))
          .username(username)
          .password(userDetails.getPassword())
          .email(request.getEmail())
          .endpoints(EndpointDetails.builder()
              .ems(ems)
              .emsUi(emsUi)
              .cdss(cdss)
              .cdss2(cdss2)
              .fhirServer(fhirServer)
              .blobServer(blobServer)
              .dos(dos)
              .logs(logs)
              .build())
          .build();
      // Create the user in cognito for ElasticSearch searching.
      cognitoService.signUp(supplierId, supplierAccountDetails);
      roleMapper.setupSupplierRoles(supplierId, supplierAccountDetails.getUsername());
      return supplierAccountDetails;
    } catch (Exception e) {
      log.error("Error creating user: {}", supplierId);
      throw e;
    }
  }

  public UserDTO getUserByUsername(String username) {
    UserEntity userEntity = userRepository.findOne(username);
    ErrorHandlingUtils.checkEntityExists(userEntity, "User");

    return new UserDTO(userEntity);
  }

  public UserEntity updateUser(UserDTO userDTO) {
    UserEntity userEntity = userRepository.findOne(userDTO.getUsername());
    ErrorHandlingUtils.checkEntityExists(userEntity, "User");

    setUserDetails(userDTO, userEntity);

    return userRepository.save(userEntity);
  }

  private void setUserDetails(UserDTO userDTO, UserEntity userEntity) {
    userEntity.setUsername(userDTO.getUsername());
    userEntity.setName(userDTO.getName());
    userEntity.setEnabled(userDTO.isEnabled());
    userEntity.setRole(userDTO.getRole());
  }

  public List<UserDTO> getAllUsers() {
    List<UserDTO> userDTOs = new ArrayList<>();

    userRepository.findAll().stream()
        .map(UserDTO::new)
        .forEach(userDTOs::add);

    return userDTOs;
  }

  public UserEntity createUser(NewUserDTO userDTO) {
    UserEntity userEntity = new UserEntity();

    if (getExistingUsernames().contains(userDTO.getUsername())) {
      throw new EMSException(HttpStatus.BAD_REQUEST, "Username already exists");
    }

    setUserDetails(userDTO, userEntity);
    userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    userEntity.setSupplierId(userDTO.getSupplierId());
    return userRepository.save(userEntity);
  }

  public UserEntity updatePassword(ChangePasswordDTO changePasswordDTO) {
    UserEntity user = userRepository.findByUsername(changePasswordDTO.getUsername());
    ErrorHandlingUtils.checkEntityExists(user, "User");
    if (passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
      user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
      return userRepository.save(user);
    } else {
      throw new EMSException(HttpStatus.UNAUTHORIZED, "Old password incorrect");
    }
  }

  public UserEntity resetPassword(ChangePasswordDTO changePasswordDTO) {
    UserEntity user = userRepository.findByUsername(changePasswordDTO.getUsername());
    ErrorHandlingUtils.checkEntityExists(user, "User");
    user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
    return userRepository.save(user);
  }

  public void deleteUser(String username) {
    userRepository.delete(username);
  }

  private List<String> getExistingUsernames() {
    return userRepository.findAll().stream()
        .map(UserEntity::getUsername)
        .collect(Collectors.toList());
  }
}
