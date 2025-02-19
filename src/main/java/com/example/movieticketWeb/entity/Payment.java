package com.example.movieticketWeb.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentID;

    @ManyToOne
    @JoinColumn(name = "perID", nullable = false)
    private Person person;

    @ManyToOne
    @JoinColumn(name = "couponID", nullable = true)
    private Coupon coupon;

	@Column(name = "totalPrice")
    private double totalPrice;

	@Column(name = "createdDate")
    private Date createdDate;

	@Column(name = "status")
    private int status;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketPayment> ticketPayments;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PopCornPayment> popCornPayments;

}
