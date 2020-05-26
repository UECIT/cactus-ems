package uk.nhs.ctp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.UserEntity;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.model.RegisterSupplierRequest;
import uk.nhs.ctp.model.SupplierAccountDetails;
import uk.nhs.ctp.model.SupplierAccountDetails.EndpointDetails;
import uk.nhs.ctp.repos.UserRepository;
import uk.nhs.ctp.service.dto.ChangePasswordDTO;
import uk.nhs.ctp.service.dto.NewUserDTO;
import uk.nhs.ctp.service.dto.UserDTO;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@Service
@RequiredArgsConstructor
public class UserManagementService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${ems.frontend}")
	private String ems;

	@Value("${cactus.cdss}")
	private String cdss;

	@Value("${dos.server}")
	private String dos;

	public SupplierAccountDetails createNewSupplierUser(RegisterSupplierRequest request) {
		var userDetails = new NewUserDTO();
		userDetails.setUsername("admin_" + request.getSupplierId());
		userDetails.setPassword(UUID.randomUUID().toString());
		userDetails.setEnabled(true);
		userDetails.setName("<Change me>");
		userDetails.setRole("ROLE_SUPPLIER_ADMIN");

		createUser(userDetails);

		return SupplierAccountDetails.builder()
				.jwt(UUID.randomUUID().toString())
				.username(userDetails.getUsername())
				.password(userDetails.getPassword())
				.endpoints(EndpointDetails.builder()
						.ems(ems)
						.cdss(cdss)
						.dos(dos)
						.build())
				.build();
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
