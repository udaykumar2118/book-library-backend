package com.library.booklibrarymanager.repository;

import com.library.booklibrarymanager.entity.Book;
import com.library.booklibrarymanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {


    List<Book> findByUser(User user);

   
    @Query("SELECT b FROM Book b WHERE b.user = :user AND " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Book> searchByUserAndTitleOrAuthor(@Param("user") User user,
                                           @Param("query") String query);

    
    List<Book> findByUserAndStatus(User user, String status);


    List<Book> findByUserAndGenre(User user, String genre);
}