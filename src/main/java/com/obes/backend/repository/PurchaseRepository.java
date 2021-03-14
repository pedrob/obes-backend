package com.obes.backend.repository;

import com.obes.backend.model.Purchase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Page<Purchase> findAllByBuyerUsernameOrderByCreatedAtDesc(Pageable pageable, String username);
}
