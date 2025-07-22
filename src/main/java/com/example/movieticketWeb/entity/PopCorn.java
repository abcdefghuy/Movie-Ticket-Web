package com.example.movieticketWeb.entity;

import java.util.List;

import jakarta.persistence.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PopCorn")
@Builder
public class PopCorn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int popcornID;

    @Column(name = "namePopCorn", columnDefinition = "NVARCHAR(500)")
    private String namePopCorn;

    @Column(name = "typePopCorn", nullable = false)
    private String typePopCorn;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "status", nullable = false)
    private boolean status;

    @OneToMany(mappedBy = "popcorn", cascade = CascadeType.ALL)
    private List<PopCornPayment> popcornPayments;
}
