package com.example.movieticketWeb.entity;

import java.util.List;

import jakarta.persistence.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Seat")
@Getter
@Setter
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int seatID;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatStatus> seatStatuses;

    @Column(name = "isCouple", nullable = false)
    private boolean isCouple = false;
}
