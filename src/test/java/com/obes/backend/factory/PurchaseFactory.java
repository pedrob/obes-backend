package com.obes.backend.factory;

import java.util.Date;
import java.util.List;

import com.obes.backend.model.ApplicationUser;
import com.obes.backend.model.Book;
import com.obes.backend.model.Purchase;

public class PurchaseFactory {

    private static int idGenerator = 0;

    public static Purchase createPurchase(ApplicationUser user, List<Book> books) {
        idGenerator++;
        return Purchase.builder()
                .id(idGenerator)
                .books(books)
                .buyer(user)
                .createdAt(new Date())
                .build();
    }
    
}
