package com.devSuperior.dsCatalog.repositories;

import com.devSuperior.dsCatalog.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
