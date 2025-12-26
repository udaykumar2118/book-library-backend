package com.library.booklibrarymanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "books")
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String genre;
    private Integer year;
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore 
    private User user;
}