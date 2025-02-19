package com.example.movieticketWeb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="cinema")
@Getter
@Setter
public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cinemaId;
    @Column(name = "cinemaName", nullable = false , columnDefinition = "NVARCHAR(500)")
    private String cinemaName;
    @Column(name = "address" , columnDefinition = "NVARCHAR(500)")
    private String address;
    @Column(name = "location", columnDefinition = "NVARCHAR(500)")
    private String location;

    @Column(name = "status", nullable = false, columnDefinition = "BIT")
    private boolean status; // New field for cinema status using bit, named "status"

    @Column(name = "roomCount", nullable = false)
    private int roomCount; // New field for the number of rooms

    @OneToMany(mappedBy = "cinema" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms;
}
