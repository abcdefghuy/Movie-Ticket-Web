package com.example.movieticketWeb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SeatStatus")
@Getter
@Setter
public class SeatStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int seatStatusId;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private MovieScreenings screening;

    @Column(name = "status", nullable = false)
    private boolean status;

    @Column(name = "bookingTime")
    private LocalDateTime bookingTime;

}
