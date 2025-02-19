package com.example.movieticketWeb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MovieScreenings")
@Getter
@Setter
public class MovieScreenings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int msID;

	@Column(nullable = false,columnDefinition = "DATE")
	private Date screeningDate; // New column for the screening date

	@Column(name = "startHour", nullable = false)
    private Date startHour;

    @Column(name = "endHour", nullable = false)
    private Date endHour;
    
    @Column(name = "status", nullable = false, columnDefinition = "BIT")
    private boolean status; // New field for cinema status using bit, named "status"

    @ManyToOne
    @JoinColumn(name = "roomID", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "movieID", nullable = false)
    private Movie movie;
    
    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL)
    private List<SeatStatus> seatStatus;

	@OneToMany(mappedBy = "movieScreenings")
    private List<Ticket> tickets;

    // Getters and setters
}
