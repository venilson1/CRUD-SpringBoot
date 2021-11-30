package com.devSuperior.dsCatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.devSuperior.dsCatalog.dto.CategoryDTO;
import com.devSuperior.dsCatalog.dto.ProductDTO;
import com.devSuperior.dsCatalog.entities.Category;
import com.devSuperior.dsCatalog.entities.Product;
import com.devSuperior.dsCatalog.repositories.CategoryRepository;
import com.devSuperior.dsCatalog.repositories.ProductRepository;
import com.devSuperior.dsCatalog.services.exceptions.DatabaseException;
import com.devSuperior.dsCatalog.services.exceptions.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

  @Autowired
  private ProductRepository repository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Transactional(readOnly = true)
  public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
    Page<Product> list = repository.findAll(pageRequest);
    return list.map(el -> new ProductDTO(el));
  }

  @Transactional(readOnly = true)
  public List<ProductDTO> findAll() {
    List<Product> list = repository.findAll();
    List<ProductDTO> listdDto = list.stream().map(el -> new ProductDTO(el)).collect(Collectors.toList());
    return listdDto;
  }

  @Transactional(readOnly = true)
  public ProductDTO findById(Long id) {
    Optional<Product> obj = repository.findById(id);
    Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity Not Found"));
    return new ProductDTO(entity, entity.getCategories());
  }

  @Transactional
  public ProductDTO insert(ProductDTO dto) {
    Product entity = new Product();
    copyDtoToEntity(dto, entity);
    entity = repository.save(entity);
    return new ProductDTO(entity);
  }

  @Transactional
  public ProductDTO update(Long id, ProductDTO dto) {
    try {
      Product entity = repository.getOne(id);
      copyDtoToEntity(dto, entity);
      entity = repository.save(entity);
      return new ProductDTO(entity);
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

  private void copyDtoToEntity(ProductDTO dto, Product entity) {

    entity.setName(dto.getName());
    entity.setDescription(dto.getDescription());
    entity.setDate(dto.getDate());
    entity.setImgUrl(dto.getImgUrl());
    entity.setPrice(dto.getPrice());

    entity.getCategories().clear();

    for (CategoryDTO catDto : dto.getCategories()) {
      Category category = categoryRepository.getOne(catDto.getId());
      entity.getCategories().add(category);
    }

  }
}
