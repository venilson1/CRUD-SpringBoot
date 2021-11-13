package com.devSuperior.dsCatalog.services;

import java.util.List;

import com.devSuperior.dsCatalog.entities.Category;
import com.devSuperior.dsCatalog.repositories.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

  @Autowired
  private CategoryRepository repository;

  public List<Category> findAll() {
    return repository.findAll();
  }
}
