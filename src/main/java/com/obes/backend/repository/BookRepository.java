package com.obes.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.obes.backend.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>{
    Page<Book> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Book> findAllByOwnerUsernameOrderByCreatedAtDesc(Pageable pageable, String username);

    Page<Book> findByTitleContaining(Pageable pageable, String term);
}
