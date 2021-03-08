package com.obes.backend.repository;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.obes.backend.model.ApplicationUser;

import java.util.Optional;

public interface ApplicationUserRepository extends PagingAndSortingRepository<ApplicationUser, Long>, JpaSpecificationExecutor<ApplicationUser>{
    Optional<ApplicationUser> findByUsername(String username);
   
}
