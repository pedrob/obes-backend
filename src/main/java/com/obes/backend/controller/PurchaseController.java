package com.obes.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import javax.validation.Valid;

import com.obes.backend.model.ApplicationUser;
import com.obes.backend.model.Purchase;
import com.obes.backend.model.Book;
import com.obes.backend.exception.NotFoundException;
import com.obes.backend.model.dto.PurchaseBodyRequest;
import com.obes.backend.service.PuchaseService;
import com.obes.backend.service.TokenService;
import com.obes.backend.repository.ApplicationUserRepository;
import com.obes.backend.repository.BookRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor(onConstructor=@__({@Autowired}))
public class PurchaseController {
    
    private PuchaseService purchaseService;
    
    private TokenService tokenService;

    private ApplicationUserRepository userRepository;

    private BookRepository bookRepository;

    @PostMapping("/purchases")
    public Purchase createPurchase(@RequestHeader("Authorization") String token,
     @Valid @RequestBody PurchaseBodyRequest purchaseBody) {
        String username = tokenService.getUsernameFromToken(token);
        Optional<ApplicationUser> buyer = userRepository.findByUsername(username);
        List<Book> books = this.getBooksFromRepository(purchaseBody.getBooksIds());
        Purchase purchase = new Purchase();
        purchase.setBooks(books);
        purchase.setBuyer(buyer.get());
        return purchaseService.makePurchase(purchase);
    }

    @GetMapping("/purchases")
    public Page<Purchase> getPurchases(@RequestHeader("Authorization") String token, 
        Pageable pageable
    ) {
        String username = tokenService.getUsernameFromToken(token);
        return purchaseService.getPurchases(pageable, username);
    }

    private List<Book> getBooksFromRepository(List<Long> booksIds) {
        List<Book> books = new ArrayList<Book>();
        for(Long bookId: booksIds) {
            Optional<Book> book = bookRepository.findById(bookId);
            if(book.isPresent()) {
                books.add(book.get());
            } else {
                throw new NotFoundException("Book with id: " + bookId + " not found");
            }
        }
        return books;
    }

}
