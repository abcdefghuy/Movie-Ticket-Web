package com.example.movieticketWeb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Ticket")
@Getter
@Setter
public class Ticket {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ticketID;

	@Column(name = "priceTicket", nullable = false)
	private double priceTicket;

	@Column(name = "date", nullable = false)
	private Date date;

	@Column(name = "placeName", nullable = false)
	private String placeName;

	@Column(name = "versionName", nullable = false)
	private String versionName;

	@Column(name = "chairNumber", nullable = false)
	private String chairNumber;

	@ManyToOne
	@JoinColumn(name = "msID")
	private MovieScreenings movieScreenings;

	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<TicketPayment> ticketPayments;
}
