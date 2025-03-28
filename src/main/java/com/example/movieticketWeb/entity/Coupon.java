package com.example.movieticketWeb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Coupon")
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int couponID;

    @Column(name = "couponName",nullable = false)
    private String couponName;

    @Column(name = "couponType",nullable = false)
    private String couponType;

    @Column(name = "couponValue",nullable = false)
    private double couponValue;

    @Column(name = "startDate",nullable = false)
    private Date startDate;

    @Column(name = "endDate",nullable = false)
    private Date endDate;

    @OneToMany(mappedBy = "coupon", orphanRemoval = false)
    private List<Payment> payments;
    @PreRemove
    private void preRemove() {
        // Hủy liên kết với tất cả các Payment trước khi xóa Coupon
        for (Payment payment : payments) {
            payment.setCoupon(null);
        }
    }

}
