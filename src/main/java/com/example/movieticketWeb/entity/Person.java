package com.example.movieticketWeb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Person")
@Getter
@Setter
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int perID;

    @Column(name = "fullName", columnDefinition = "nvarchar(50)", nullable = true) // Cho phép null
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "nvarchar(50)") // email vẫn không cho phép null
    private String email;

    @Column(name = "password", nullable = false, columnDefinition = "nvarchar(50)") // password vẫn không cho phép null
    private String password;

    @Column(name = "phone", columnDefinition = "nvarchar(15)", nullable = true) // Cho phép null
    private String phone;

    @Column(name = "role", columnDefinition = "nvarchar(20)", nullable = true) // Cho phép null
    private String role;

    @Column(name = "gender", nullable = true) // Cho phép null
    private Integer gender; // Sử dụng Integer thay vì int để cho phép null

    @Column(name = "birthDate", nullable = true) // Cho phép null
    private Date birthDate;

    @Column(name = "region", columnDefinition = "nvarchar(50)", nullable = true) // Cho phép null
    private String region;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Message> messages;
}

