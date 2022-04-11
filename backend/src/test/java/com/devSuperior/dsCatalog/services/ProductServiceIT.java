package com.devSuperior.dsCatalog.services;

import com.devSuperior.dsCatalog.dto.ProductDTO;
import com.devSuperior.dsCatalog.repositories.ProductRepository;
import com.devSuperior.dsCatalog.services.exceptions.ResourceNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIT {

  @Autowired
  private ProductService service;

  @Autowired
  private ProductRepository repository;

  private Long existsId;
  private Long notExistsId;
  private Long countTotalProducts;

  @BeforeEach
  void setUp() throws Exception {

    existsId = 1L;
    notExistsId = 1000L;
    countTotalProducts = 25L;
  }

  @Test
  public void deleteShouldDeleteResourceWhenIdExists() {

    service.delete(existsId);

    Assertions.assertEquals(countTotalProducts - 1, repository.count());

  }

  @Test
  public void deleteShouldReturnResourceNotGoundExceptionWhenIdDoesNotExists() {

    Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      service.delete(notExistsId);

    });

  }

  @Test
  public void findAllPageShouldReturnPageWhenPageZeroAndSizeTen() {

    PageRequest pageRequest = PageRequest.of(0, 10);

    Page<ProductDTO> result = service.findAllPaged(0L, "", pageRequest);

    Assertions.assertFalse(result.isEmpty());
    Assertions.assertEquals(0, result.getNumber());
    Assertions.assertEquals(10, result.getSize());
    Assertions.assertEquals(countTotalProducts, result.getTotalElements());

  }

  @Test
  public void findAllPageShouldReturnEmptyWhenPageDoesNotExists() {

    PageRequest pageRequest = PageRequest.of(50, 10);

    Page<ProductDTO> result = service.findAllPaged(0L, "", pageRequest);

    Assertions.assertTrue(result.isEmpty());

  }

  @Test
  public void findAllPageShouldReturnSortedPageWhenSortByName() {

    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

    Page<ProductDTO> result = service.findAllPaged(0L, "", pageRequest);

    Assertions.assertFalse(result.isEmpty());
    Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
    Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
    Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
  }

}
