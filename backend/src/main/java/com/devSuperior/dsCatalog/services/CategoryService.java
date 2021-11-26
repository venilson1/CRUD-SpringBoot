package com.devSuperior.dsCatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.devSuperior.dsCatalog.dto.CategoryDTO;
import com.devSuperior.dsCatalog.entities.Category;
import com.devSuperior.dsCatalog.repositories.CategoryRepository;
import com.devSuperior.dsCatalog.services.exceptions.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

  @Autowired
  private CategoryRepository repository;

  @Transactional(readOnly = true)
  public List<CategoryDTO> findAll() {

    List<Category> list = repository.findAll();
    List<CategoryDTO> listdDto = list.stream().map(el -> new CategoryDTO(el)).collect(Collectors.toList());

    return listdDto;
  }

  @Transactional(readOnly = true)
  public CategoryDTO findById(Long id) {
    Optional<Category> obj = repository.findById(id);
    Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity Not Found"));
    return new CategoryDTO(entity);
  }

  @Transactional
  public CategoryDTO insert(CategoryDTO dto) {
    Category entity = new Category();
    entity.setName(dto.getName());
    entity = repository.save(entity);
    return new CategoryDTO(entity);
  }

  @Transactional
  public CategoryDTO update(Long id, CategoryDTO dto) {
    try {
      Category entity = repository.getOne(id);
      entity.setName(dto.getName());
      entity = repository.save(entity);
      return new CategoryDTO(entity);
    } catch (EntityNotFoundException e) {
      throw new ResourceNotFoundException("Id Not Found " + id);
    }
  }

}
