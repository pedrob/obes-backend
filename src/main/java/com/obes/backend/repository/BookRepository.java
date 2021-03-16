package com.obes.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.obes.backend.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

    @Query("SELECT b FROM Book b WHERE b.purchase = null")
    Page<Book> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Book> findAllByOwnerUsernameOrderByCreatedAtDesc(Pageable pageable, String username);

    @Query("SELECT b FROM Book b WHERE b.purchase.buyer.username = :username")
    Page<Book> findAllByBuyerUsername(Pageable pageable, @Param("username") String username);

    Page<Book> findByTitleContainingIgnoreCase(Pageable pageable, String term);
}
