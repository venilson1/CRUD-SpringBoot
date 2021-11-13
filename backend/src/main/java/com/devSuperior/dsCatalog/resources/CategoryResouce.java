package com.devSuperior.dsCatalog.resources;

import java.util.List;

import com.devSuperior.dsCatalog.entities.Category;
import com.devSuperior.dsCatalog.services.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResouce {

  @Autowired
  private CategoryService service;

  @GetMapping
  public ResponseEntity<List<Category>> findAll() {
    List<Category> list = service.findAll();
    return ResponseEntity.ok().body(list);
  }
}
