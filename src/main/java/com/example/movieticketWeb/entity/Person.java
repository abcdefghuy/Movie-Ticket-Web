package com.example.movieticketWeb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Person")
public class Person implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int perID;

    @Column(name = "fullName", columnDefinition = "nvarchar(50)", nullable = true) // Cho phép null
    private String username;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "nvarchar(50)") // email vẫn không cho phép null
    private String email;

    @Column(name = "password", nullable = false, columnDefinition = "nvarchar(500)") // password vẫn không cho phép null
    private String password;

    @Column(name = "phone", columnDefinition = "nvarchar(15)", nullable = true) // Cho phép null
    private String phone;

    @Column(name = "role", columnDefinition = "nvarchar(20)", nullable = true) // Cho phép null
    private String role;

    @Column(name = "gender", nullable = true) // Cho phép null
    private Integer gender; // Sử dụng Integer thay vì int để cho phép null

    @Column(name = "birthDate", nullable = true) // Cho phép null
    private LocalDate birthDate;

    @Column(name = "region", columnDefinition = "nvarchar(50)", nullable = true) // Cho phép null
    private String region;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Message> messages;

    @Column(name = "verification_code")
    private String verificationCode;
    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpiresAt;
    private boolean enabled;

    public Person(String username, String email, String password, String phone, String role, Integer gender, LocalDate birthDate, String region) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.gender = gender;
        this.birthDate = birthDate;
        this.region = region;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    //TODO: add proper boolean checks
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

