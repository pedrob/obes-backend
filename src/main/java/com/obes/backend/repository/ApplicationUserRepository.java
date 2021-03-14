package com.obes.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.obes.backend.model.ApplicationUser;

import java.util.Optional;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByUsername(String username);  
}
