package com.library.booklibrarymanager.service;

import com.library.booklibrarymanager.entity.Book;
import com.library.booklibrarymanager.entity.User;
import com.library.booklibrarymanager.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

  
    public List<Book> getUserBooks(User user) {
        return bookRepository.findByUser(user);
    }

  
    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

  
    public void deleteBook(Long id, User user) {
        Book book = getBookById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        bookRepository.delete(book);
    }

    
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    
    public List<Book> searchBooks(User user, String query) {
        return bookRepository.searchByUserAndTitleOrAuthor(user, query);
    }

   
    public List<Book> getBooksByStatus(User user, String status) {
        return bookRepository.findByUserAndStatus(user, status);
    }

   
    public List<Book> getBooksByGenre(User user, String genre) {
        return bookRepository.findByUserAndGenre(user, genre);
    }

   
    public long countBooksByStatus(User user, String status) {
        List<Book> books = bookRepository.findByUserAndStatus(user, status);
        return (books != null) ? books.size() : 0;
    }

   
    public long countBooksByGenre(User user, String genre) {
        List<Book> books = bookRepository.findByUserAndGenre(user, genre);
        return (books != null) ? books.size() : 0;
    }

    
    public long countTotalBooks(User user) {
        List<Book> books = bookRepository.findByUser(user);
        return (books != null) ? books.size() : 0;
    }
}