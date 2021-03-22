package com.obes.backend.factory;

import java.util.Date;

import com.obes.backend.model.ApplicationUser;
import com.obes.backend.model.Book;

public class BookFactory {

    private static int idGenerator = 0;
    
    public static Book createBook(ApplicationUser user) {
        idGenerator++;
        return Book.builder()
                .id(idGenerator)
                .title("Title " + idGenerator)
                .author("Author " + idGenerator)
                .description("Description " + idGenerator)
                .price((float) 20)
                .owner(user)
                .createdAt(new Date())
                .build();
    }

}
