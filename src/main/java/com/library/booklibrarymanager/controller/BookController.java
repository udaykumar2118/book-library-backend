package com.library.booklibrarymanager.controller;

import com.library.booklibrarymanager.dto.BookDto;
import com.library.booklibrarymanager.entity.Book;
import com.library.booklibrarymanager.entity.User;
import com.library.booklibrarymanager.service.BookService;
import com.library.booklibrarymanager.service.EmailService;
import com.library.booklibrarymanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

   
    private User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof String email)) {
            throw new RuntimeException("Unauthenticated");
        }
        User user = userService.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");
        return user;
    }

    
    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        User user = getCurrentUser();
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", bookService.countTotalBooks(user));
        stats.put("reading", bookService.countBooksByStatus(user, "Reading"));
        stats.put("completed", bookService.countBooksByStatus(user, "Completed"));
        stats.put("wishlist", bookService.countBooksByStatus(user, "Wishlist"));
        return stats;
    }

   
   
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        User user = getCurrentUser();
        
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return book;
    }

  
    @PostMapping
    public Book addBook(@RequestBody BookDto dto) {
        User user = getCurrentUser();

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setGenre(dto.getGenre());
        book.setYear(dto.getYear());
        book.setStatus(dto.getStatus());
        book.setUser(user);

        Book savedBook = bookService.addBook(book);

        
        try {
            emailService.sendEmail(
                user.getEmail(),
                "New Book Added to Your Library",
                "Hello " + user.getName() + ",\n\n" +
                "You have successfully added \"" + savedBook.getTitle() + "\" to your library.\n\n" +
                "Book Details:\n" +
                "Title: " + savedBook.getTitle() + "\n" +
                "Author: " + savedBook.getAuthor() + "\n" +
                "Genre: " + savedBook.getGenre() + "\n" +
                "Year: " + savedBook.getYear() + "\n" +
                "Status: " + savedBook.getStatus() + "\n\n" +
                "Happy Reading!\n" +
                "Book Library Manager Team"
            );
        } catch (Exception e) {
            System.err.println("Failed to send book added email: " + e.getMessage());
            
        }

        return savedBook;
    }

   
    @GetMapping
    public List<Book> getBooks() {
        return bookService.getUserBooks(getCurrentUser());
    }

  
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id,
                           @RequestBody BookDto dto) {
        User user = getCurrentUser();

        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setGenre(dto.getGenre());
        book.setYear(dto.getYear());
        book.setStatus(dto.getStatus());

        return bookService.updateBook(book);
    }

    
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        User user = getCurrentUser();

        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        bookService.deleteBook(id, user);
    }

 
    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam String query) {
        User user = getCurrentUser();
        return bookService.searchBooks(user, query);
    }

  
    @GetMapping("/status/{status}")
    public List<Book> getBooksByStatus(@PathVariable String status) {
        User user = getCurrentUser();
        return bookService.getBooksByStatus(user, status);
    }

   
    @GetMapping("/genre/{genre}")
    public List<Book> getBooksByGenre(@PathVariable String genre) {
        User user = getCurrentUser();
        return bookService.getBooksByGenre(user, genre);
    }

   
    @GetMapping("/genre-stats")
    public Map<String, Long> getGenreStats() {
        User user = getCurrentUser();
        Map<String, Long> stats = new HashMap<>();
        
     
        List<Book> books = bookService.getUserBooks(user);
        
        
        for (Book book : books) {
            String genre = book.getGenre();
            if (genre != null && !genre.trim().isEmpty()) {
                stats.put(genre, stats.getOrDefault(genre, 0L) + 1);
            }
        }
        
        return stats;
    }
}