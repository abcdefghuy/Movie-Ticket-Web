package com.example.movieticketWeb.entity;

import java.util.Date;

import jakarta.persistence.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NewsOrDiscount")
@Builder
public class NewsOrDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int newsID;

    @Column(name = "title", columnDefinition = "NVARCHAR(500)")
    private String title;

    @Column(name = "description", columnDefinition = "NVARCHAR(500)")
    private String description;

    @Column(name = "detail", columnDefinition = "NVARCHAR(1000)")
    private String detail;

    @Column(name = "images", columnDefinition = "NVARCHAR(500)")
    private String images;

    @Column(name = "author", columnDefinition = "NVARCHAR(500)")
    private String author;

    @Column(name = "date", nullable = false)
    private Date date;
}
