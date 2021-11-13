package com.devSuperior.dsCatalog.resources;

import java.util.ArrayList;
import java.util.List;

import com.devSuperior.dsCatalog.entities.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResouce {

  @GetMapping
  public ResponseEntity<List<Category>> findAll() {
    List<Category> list = new ArrayList<>();
    list.add(new Category(1L, "books"));
    list.add(new Category(2L, "Eletronics"));
    return ResponseEntity.ok().body(list);
  }
}
