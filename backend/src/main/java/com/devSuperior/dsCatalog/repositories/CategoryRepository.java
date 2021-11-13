package com.devSuperior.dsCatalog.repositories;

import com.devSuperior.dsCatalog.entities.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
