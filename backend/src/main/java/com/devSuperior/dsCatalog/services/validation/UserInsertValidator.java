package com.devSuperior.dsCatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.devSuperior.dsCatalog.dto.UserInsertDTO;
import com.devSuperior.dsCatalog.entities.User;
import com.devSuperior.dsCatalog.repositories.UserRepository;
import com.devSuperior.dsCatalog.resources.exceptions.FieldMessage;

import org.springframework.beans.factory.annotation.Autowired;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

  @Autowired
  private UserRepository repository;

  @Override
  public void initialize(UserInsertValid ann) {
  }

  @Override
  public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

    List<FieldMessage> list = new ArrayList<>();

    // Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à
    // lista

    User user = repository.findByEmail(dto.getEmail());

    if (user != null) {
      list.add(new FieldMessage("email", "Email já existe"));
    }

    for (FieldMessage e : list) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
          .addConstraintViolation();
    }
    return list.isEmpty();
  }
}
