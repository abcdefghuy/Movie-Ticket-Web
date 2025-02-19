package com.example.movieticketWeb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Movie")
@Getter
@Setter
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int movieID;

    @Column(name = "movieName", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String movieName;

    @Column(name = "category", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String category;

    @Column(name = "description", columnDefinition = "NVARCHAR(1000)")
    private String description;

    @Column(name = "image", columnDefinition = "NVARCHAR(255)")
    private String image;

    @Column(name = "movieDuration",columnDefinition = "NVARCHAR(20)")
    private String movieDuration;

    @Column(name = "releaseDay", nullable = false)
    private Date releaseDay;

    @Column(name = "rating", nullable = false)
    private float rating;

    @Column(name = "status", nullable = false)
    private boolean status;
    
    @Column(name = "movieTrailer", columnDefinition = "NVARCHAR(1000)")
    private String movieTrailer;

    @OneToMany(mappedBy = "movie" ,cascade = CascadeType.ALL)
    private List<MovieScreenings> movieScreenings;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Review> reviews;

}
