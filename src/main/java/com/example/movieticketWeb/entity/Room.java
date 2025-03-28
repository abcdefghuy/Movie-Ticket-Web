package com.example.movieticketWeb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="room")
@Getter
@Setter
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roomId;
    @Column(name = "roomName",nullable = false)
    private String roomName;

    @Column(name="screenType",nullable = false)
    private String screenType;

    @Column(name = "chairNumber")
    private Integer chairNumber;

    @Column(name = "status", nullable = false, columnDefinition = "BIT")
    private boolean status; // New field for cinema status using bit, named "status"

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Seat> seat;

    @ManyToOne
    @JoinColumn(name = "cinemaID", nullable = false)
    private Cinema cinema;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MovieScreenings> movieScreenings;
}
