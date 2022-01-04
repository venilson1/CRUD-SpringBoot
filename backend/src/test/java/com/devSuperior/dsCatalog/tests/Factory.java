package com.devSuperior.dsCatalog.tests;

import java.time.Instant;

import com.devSuperior.dsCatalog.dto.ProductDTO;
import com.devSuperior.dsCatalog.entities.Category;
import com.devSuperior.dsCatalog.entities.Product;

public class Factory {

  public static Product createProduct() {
    Product product = new Product(
        1L, "Phone", "Good Phone", 800.0, "http://img.com/img.png",
        Instant.parse("2020-10-20T03:00:00Z"));

    product.getCategories().add(createCategory());
    return product;
  }

  public static ProductDTO createProductDTO() {
    Product product = createProduct();
    return new ProductDTO(product, product.getCategories());
  }

  public static Category createCategory() {
    Category category = new Category(2L, "Electronics");
    return category;
  }

}
