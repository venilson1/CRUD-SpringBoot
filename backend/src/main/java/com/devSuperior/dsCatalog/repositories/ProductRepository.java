package com.devSuperior.dsCatalog.repositories;

import com.devSuperior.dsCatalog.entities.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
