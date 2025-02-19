package com.example.movieticketWeb.entity;


import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TicketPayment")
@Getter
@Setter
public class TicketPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentTicketID;

    @ManyToOne
    @JoinColumn(name = "paymentID")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "ticketID")
    private Ticket ticket;
    
}
