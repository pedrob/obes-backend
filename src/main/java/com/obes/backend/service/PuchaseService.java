package com.obes.backend.service;

import com.obes.backend.model.Purchase;
import com.obes.backend.model.Book;
import com.obes.backend.repository.PurchaseRepository;
import com.obes.backend.exception.SameBookException;
import com.obes.backend.exception.SameBuyerAndOwnerException;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import java.util.ArrayList;

@Service
@AllArgsConstructor(onConstructor=@__({@Autowired}))
public class PuchaseService {

    private PurchaseRepository purchaseRepository;
    
    @Transactional
    public Purchase makePurchase(Purchase purchase) 
    throws SameBuyerAndOwnerException, SameBookException {
        this.validatePurchase(purchase);
        purchase.setCreatedAt(new Date());
        return purchaseRepository.save(purchase);
    }

    public Page<Purchase> getPurchases(Pageable pageable, String username) {
        return purchaseRepository.findAllByBuyerUsernameOrderByCreatedAtDesc(pageable, username);
    }

    private void validatePurchase(Purchase purchase) 
    throws SameBuyerAndOwnerException, SameBookException {
        List<Book> purchasedBooks = purchase.getBooks();
        Long buyerId = purchase.getBuyer();
        this.validatePurchasedBooks(purchasedBooks, buyerId);
    }

    private void validatePurchasedBooks(List<Book> purchasedBooks, Long buyerId) 
    throws SameBuyerAndOwnerException, SameBookException {
        List<Long> booksIds = new ArrayList<Long>();
        for (Book book : purchasedBooks) {
            if (book.getOwner() == buyerId) {
                throw new SameBuyerAndOwnerException("Sorry, but you is already the owner of one this books");
            }
            booksIds.add(book.getId());
        }
        HashSet<Long> bookIdsSet = new HashSet<Long>(booksIds);
        if (bookIdsSet.size() != booksIds.size()) {
            throw new SameBookException("Sorry, but you has the same book in the list");
        }
    }

}
