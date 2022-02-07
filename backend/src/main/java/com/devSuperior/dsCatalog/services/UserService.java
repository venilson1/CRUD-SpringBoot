package com.devSuperior.dsCatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.devSuperior.dsCatalog.dto.RoleDTO;
import com.devSuperior.dsCatalog.dto.UserDTO;
import com.devSuperior.dsCatalog.dto.UserInsertDTO;
import com.devSuperior.dsCatalog.dto.UserUpdateDTO;
import com.devSuperior.dsCatalog.entities.Role;
import com.devSuperior.dsCatalog.entities.User;
import com.devSuperior.dsCatalog.repositories.RoleRepository;
import com.devSuperior.dsCatalog.repositories.UserRepository;
import com.devSuperior.dsCatalog.services.exceptions.DatabaseException;
import com.devSuperior.dsCatalog.services.exceptions.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
	
	private static Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private UserRepository repository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Transactional(readOnly = true)
  public Page<UserDTO> findAllPaged(Pageable pageable) {
    Page<User> list = repository.findAll(pageable);
    return list.map(el -> new UserDTO(el));
  }

  @Transactional(readOnly = true)
  public List<UserDTO> findAll() {
    List<User> list = repository.findAll();
    List<UserDTO> listdDto = list.stream().map(el -> new UserDTO(el)).collect(Collectors.toList());
    return listdDto;
  }

  @Transactional(readOnly = true)
  public UserDTO findById(Long id) {
    Optional<User> obj = repository.findById(id);
    User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity Not Found"));
    return new UserDTO(entity);
  }

  @Transactional
  public UserDTO insert(UserInsertDTO dto) {
    User entity = new User();
    copyDtoToEntity(dto, entity);
    entity.setPassword(passwordEncoder.encode(dto.getPassword()));
    entity = repository.save(entity);
    return new UserDTO(entity);
  }

  @Transactional
  public UserDTO update(Long id, UserUpdateDTO dto) {
    try {
      User entity = repository.getOne(id);
      copyDtoToEntity(dto, entity);
      entity = repository.save(entity);
      return new UserDTO(entity);
    } catch (EntityNotFoundException e) {
      throw new ResourceNotFoundException("Id Not Found " + id);
    }
  }

  public void delete(Long id) {
    try {
      repository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new ResourceNotFoundException("Id Not Found " + id);
    } catch (DataIntegrityViolationException e) {
      throw new DatabaseException("Integrity violation");
    }
  }

  private void copyDtoToEntity(UserDTO dto, User entity) {

    entity.setFirstName(dto.getFirstName());
    entity.setLastName(dto.getLastName());
    entity.setEmail(dto.getEmail());

    entity.getRoles().clear();
    for (RoleDTO roleDto : dto.getRoles()) {
      Role role = roleRepository.getOne(roleDto.getId());
      entity.getRoles().add(role);
    }

  }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByEmail(username);
		
		if(user == null) {
			logger.error("user not found: " + username);
			throw new UsernameNotFoundException("Email not found!");
		}
		logger.info("user found: " + username);
		return user;
	}
}
