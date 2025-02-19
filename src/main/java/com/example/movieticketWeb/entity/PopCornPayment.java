package com.example.movieticketWeb.entity;


import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PopcornPayment")
@Getter
@Setter
public class PopCornPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentPopCornID;

    @ManyToOne
    @JoinColumn(name = "paymentID", nullable = false)
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "popcornID", nullable = false)
    private PopCorn popcorn;

    @Column(name = "quantity", nullable = false)
    private int quantity;
    
}
