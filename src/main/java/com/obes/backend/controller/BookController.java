package com.obes.backend.controller;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

import javax.validation.Valid;

import com.obes.backend.model.ApplicationUser;
import com.obes.backend.model.Book;
import com.obes.backend.repository.ApplicationUserRepository;
import com.obes.backend.repository.BookRepository;
import com.obes.backend.service.FileService;
import com.obes.backend.service.TokenService;
import com.obes.backend.exception.NotFoundException;

@RestController
@AllArgsConstructor(onConstructor=@__({@Autowired}))
public class BookController {

    private BookRepository bookRepository;
    
    private TokenService tokenService;

    private ApplicationUserRepository userRepository;

    private FileService fileService;
    
    @GetMapping("/books")
    public Page<Book> getBooks(Pageable pageable) {
        return bookRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @GetMapping("/books/owner")
    public Page<Book> getBooksByOwner(@RequestHeader("Authorization") String token, Pageable pageable) {
        String ownerUsername = tokenService.getUsernameFromToken(token);
        return bookRepository.findAllByOwnerUsernameOrderByCreatedAtDesc(pageable, ownerUsername);
    }

    @GetMapping("/books/buyer")
    public Page<Book> getBooksByBuyer(@RequestHeader("Authorization") String token, Pageable pageable) {
        String buyerUsername = tokenService.getUsernameFromToken(token);
        return bookRepository.findAllByBuyerUsername(pageable, buyerUsername);
    }

    @GetMapping("/books/{bookId}")
    public Book getBookById(@PathVariable Long bookId) {
        return bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("Book not found"));
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@RequestHeader("Authorization") String token, @RequestBody @Valid Book book) {
        String username = tokenService.getUsernameFromToken(token);
        Optional<ApplicationUser> user = userRepository.findByUsername(username);
        book.setOwner(user.get());
        book.setCreatedAt(new Date());
        return bookRepository.save(book);
    }

    @PutMapping("/books/{bookId}")
    public Book uploadBook(@PathVariable Long bookId, @Valid @RequestBody Book bookRequest) {
        return bookRepository.findById(bookId).map(book -> {
            book.setTitle(bookRequest.getTitle());
            book.setAuthor(bookRequest.getAuthor());
            book.setDescription(bookRequest.getDescription());
            book.setPrice(bookRequest.getPrice());
            return bookRepository.save(book);
        }).orElseThrow(() -> new NotFoundException("Book not found"));
    }

    @DeleteMapping("/books/{bookId}")
    public void deleteBook(@PathVariable Long bookId) {
        try {
            bookRepository.deleteById(bookId);
        } catch (Exception e) {
            throw new NotFoundException("Book not found");    
        } 
    }

    @GetMapping("/books/search")
    public Page<Book> searchBooks(Pageable pageable, @RequestParam("term") String term) {
        return bookRepository.findByTitleContainingIgnoreCase(pageable, term);
    }

    @GetMapping("/books/search_by_author")
    public Page<Book> searchBooksByAuthor(Pageable pageable, @RequestParam("term") String term) {
        return bookRepository.findByAuthorContainingIgnoreCase(pageable, term);
    }

    @PostMapping("/books/image")
    public void searchBooksByAuthor(@RequestParam("file") MultipartFile file, Long id) {
        fileService.uploadFile(file, id);
        Book book = bookRepository.getOne(id);
        //TODO: change to domain
        book.setImageUrl("http://localhost:8080/uploads/"+
        id+"/"+StringUtils.cleanPath(file.getOriginalFilename()));
        bookRepository.save(book);
    }

    @GetMapping("/uploads/{id}/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id, @PathVariable String imageName) {
        byte[] image = fileService.getFile("uploads/"+id+"/"+imageName);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
}
