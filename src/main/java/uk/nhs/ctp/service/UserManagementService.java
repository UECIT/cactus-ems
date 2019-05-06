package uk.nhs.ctp.service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javassist.NotFoundException;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.entities.UserEntity;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.UserRepository;
import uk.nhs.ctp.service.dto.ChangePasswordDTO;
import uk.nhs.ctp.service.dto.NewUserDTO;
import uk.nhs.ctp.service.dto.UserDTO;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@Service
public class UserManagementService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CdssSupplierService cdssSupplierService;

	public UserDTO getUserByUsername(String username) throws NotFoundException {
		UserEntity userEntity = userRepository.findOne(username);
		ErrorHandlingUtils.checkEntityExists(userEntity, "User");

		return convertToUserDTO(userEntity);
	}

	public UserEntity updateUser(UserDTO userDTO) throws Exception {
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
		userEntity.setCdssSuppliers(getCdssSuppliers(userDTO));
	}

	private List<CdssSupplier> getCdssSuppliers(UserDTO userDTO) {
		return userDTO.getCdssSuppliers().stream()
				.map(supplierDTO -> cdssSupplierService.findBySupplierId(supplierDTO.getId()))
				.collect(Collectors.toList());
	}

	public List<UserDTO> getAllUsers() {
		List<UserDTO> userDTOs = new ArrayList<>();

		userRepository.findAll().forEach(entity -> {
			userDTOs.add(convertToUserDTO(entity));
		});

		return userDTOs;
	}

	private UserDTO convertToUserDTO(UserEntity entity) {
		UserDTO userDTO = new UserDTO(entity);
		userDTO.setCdssSuppliers(cdssSupplierService.convertToSupplierDTO(entity.getCdssSuppliers()));
		return userDTO;
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

	public UserEntity updatePassword(ChangePasswordDTO changePasswordDTO) throws UserPrincipalNotFoundException {
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

	public List<String> getExistingUsernames() {
		return userRepository.findAll().stream().map(userEntity -> userEntity.getUsername())
				.collect(Collectors.toList());
	}
}
