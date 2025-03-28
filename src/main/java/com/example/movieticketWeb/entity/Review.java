package com.example.movieticketWeb.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Review")
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewID;

    @Column(name = "content", nullable = false, columnDefinition = "NVARCHAR(1000)")
    private String content;

    @Column(name = "evaluate",nullable = false)
    private float evaluate;

    @ManyToOne
    @JoinColumn(name = "movieID", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "perID", nullable = false)
    private Person person;

}
