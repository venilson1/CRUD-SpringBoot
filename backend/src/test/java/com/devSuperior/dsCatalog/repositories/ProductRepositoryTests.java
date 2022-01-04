package com.devSuperior.dsCatalog.repositories;

import java.util.Optional;

import com.devSuperior.dsCatalog.entities.Product;
import com.devSuperior.dsCatalog.tests.Factory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

@DataJpaTest
public class ProductRepositoryTests {

  @Autowired
  private ProductRepository repository;

  @Test
  public void saveShouldPersistAutoincrementWhenIdIsNull() {
    Product product = Factory.createProduct();
    long countTotalProducts = 25L;

    product.setId(null);

    product = repository.save(product);

    Assertions.assertNotNull(product.getId());
    Assertions.assertEquals(countTotalProducts + 1, product.getId());
  }

  @Test
  public void OptionalProductShouldReturnWhenIdExist() {

    long existsId = 1L;

    Optional<Product> result = repository.findById(existsId);

    Assertions.assertTrue(result.isPresent());
  }

  @Test
  public void OptionalProductShouldReturnEmptyWhenIdNotExist() {
    long notExistsId = 1000L;

    Optional<Product> result = repository.findById(notExistsId);

    Assertions.assertFalse(result.isPresent());
  }

  @Test
  public void deleteShouldDeleteObjectWhenIdExists() {

    long existsId = 1L;

    repository.deleteById(existsId);

    Optional<Product> result = repository.findById(existsId);

    Assertions.assertFalse(result.isPresent());
  }

  @Test
  public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {

    Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
      long notExistsId = 1000L;
      repository.deleteById(notExistsId);
    });
  }

}
