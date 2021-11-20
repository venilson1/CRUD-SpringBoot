package com.devSuperior.dsCatalog.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.devSuperior.dsCatalog.dto.CategoryDTO;
import com.devSuperior.dsCatalog.entities.Category;
import com.devSuperior.dsCatalog.repositories.CategoryRepository;

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

    // List<CategoryDTO> listdDto = new ArrayList<>();
    // for (Category cat : list) {
    // listdDto.add(new CategoryDTO(cat));
    // }

    return listdDto;
  }
}
