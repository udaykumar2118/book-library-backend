package com.library.booklibrarymanager.dto;

import lombok.Data;

@Data
public class BookDto {
    private String title;
    private String author;
    private String genre;
    private Integer year;
    private String status; // Reading, Completed, Wishlist
}
