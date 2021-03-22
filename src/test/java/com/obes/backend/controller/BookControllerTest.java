package com.obes.backend.controller;

import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.obes.backend.factory.ApplicationUserFactory;
import com.obes.backend.factory.BookFactory;
import com.obes.backend.factory.PurchaseFactory;
import com.obes.backend.model.ApplicationUser;
import com.obes.backend.model.Book;
import com.obes.backend.model.Purchase;
import com.obes.backend.repository.BookRepository;
import com.obes.backend.service.PurchaseService;
import com.obes.backend.service.TokenService;
import com.obes.backend.repository.ApplicationUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.Matchers.is;


@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private TokenService tokenService;

    private static boolean setupAlreadyCalled = false;

    @BeforeEach
    public void setUp() {
        if (!setupAlreadyCalled) {
            ApplicationUser user = ApplicationUserFactory.createUser("user", "password");
            ApplicationUser user2 = ApplicationUserFactory.createUser("user2", "password");
            userRepository.save(user);
            userRepository.save(user2);
            
            Book book1 = BookFactory.createBook(user);
            Book book2 = BookFactory.createBook(user);
            
            bookRepository.save(book1);
            bookRepository.save(book2);
    
            Book book3 = BookFactory.createBook(user);
            Purchase purchase = PurchaseFactory.createPurchase(user2, Arrays.asList(book3));
            book3.setPurchase(purchase);
            bookRepository.save(book3);
            purchaseService.makePurchase(purchase);

            setupAlreadyCalled = true;
        }
    }

    @Test
    public void shouldGetAllBooksNotPurchased() throws Exception {
        mockMvc.perform(get("/books")).andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    public void shouldGetBooksByOwnerUsername() throws Exception {
        String token = tokenService.createToken("user");

        mockMvc.perform(get("/books/owner").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Test
    public void shouldGetBooksByBuyerUsername() throws Exception {
        String token = tokenService.createToken("user2");

        mockMvc.perform(get("/books/buyer").header("Authorization", "Bearer " + token)).andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].id", is(3)));
    }

    @Test
    public void shouldGetBookById() throws Exception {
        mockMvc.perform(get("/books/1")).andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    public void shouldGetBookForSearchTerm() throws Exception {
        mockMvc.perform(get("/books/search?term=3")).andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].id", is(3)));
    }

    @Test
    public void shouldCreateABookSuccessfully() throws Exception {
        String token = tokenService.createToken("user2");

        mockMvc.perform(post("/books").header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                 put("title", "Title Test");
                 put("author", "Author Test");
                 put("description", "Description test");
                 put("price", 30);
            }})))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title", is("Title Test")))
            .andExpect(jsonPath("$.author", is("Author Test")))
            .andExpect(jsonPath("$.description", is("Description test")))
            .andExpect(jsonPath("$.price", is(30.0)));
    }
    
    @Test
    public void shouldUpdateABookSuccessfully() throws Exception {
        ApplicationUser user = ApplicationUserFactory.createUser("fakeuserupdate", "password");
        userRepository.save(user);
        Book book = BookFactory.createBook(user);
        bookRepository.save(book);
        String token = tokenService.createToken("user2");

        mockMvc.perform(put("/books/" + book.getId()).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                 put("title", "Title2 Test");
                 put("author", "Author2 Test");
                 put("description", "Description2 test");
                 put("price", 40);
            }})))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is((int)book.getId())))
            .andExpect(jsonPath("$.title", is("Title2 Test")))
            .andExpect(jsonPath("$.author", is("Author2 Test")))
            .andExpect(jsonPath("$.description", is("Description2 test")))
            .andExpect(jsonPath("$.price", is(40.0)));

        bookRepository.delete(book);
        userRepository.delete(user);
    }

    @Test
    public void shouldDeleteABookSuccessfully() throws Exception {
        ApplicationUser user = ApplicationUserFactory.createUser("fakeuserdelete", "password");
        userRepository.save(user);
        Book book = BookFactory.createBook(user);
        bookRepository.save(book);

        String token = tokenService.createToken("fakeuserdelete");

        mockMvc.perform(delete("/books/" + book.getId()).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
        
        bookRepository.delete(book);
        userRepository.delete(user);
    }

    //TODO: should got a better status response
    @Test
    public void shouldFailToGetABookByOwnerWithoutToken() throws Exception {
        mockMvc.perform(get("/books/owner"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailToGetABookByBuyerWithoutToken() throws Exception {
        mockMvc.perform(get("/books/buyer"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailToGetBookWithWrongId() throws Exception {
        mockMvc.perform(get("/books/22"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void shouldFailToDeleteBookWithWrongId() throws Exception {
        String token = tokenService.createToken("user");

        mockMvc.perform(delete("/books/22").header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound());
    }

    @Test
    public void shouldFailToDeleteBookWithoutToken() throws Exception {
        mockMvc.perform(delete("/books/2"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void shouldFailToCreateABookWithoutToken() throws Exception {

        mockMvc.perform(post("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                 put("title", "Title Test");
                 put("author", "Author Test");
                 put("description", "Description test");
                 put("price", 30);
            }})))
            .andExpect(status().isForbidden());
    }

    @Test
    public void shouldFailToUpdateABookWithoutToken() throws Exception {
        ApplicationUser user = ApplicationUserFactory.createUser("fakeuserupdate", "password");
        userRepository.save(user);
        Book book = BookFactory.createBook(user);
        bookRepository.save(book);

        mockMvc.perform(put("/books/" + book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(new HashMap<>()
            {{
                 put("title", "Title2 Test");
                 put("author", "Author2 Test");
                 put("description", "Description2 test");
                 put("price", 40);
            }})))
            .andExpect(status().isForbidden());

        bookRepository.delete(book);
        userRepository.delete(user);
    }

}
