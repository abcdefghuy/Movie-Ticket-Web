package com.example.movieticketWeb.entity;

import java.util.List;

import jakarta.persistence.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Conversation")
@Getter
@Setter
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int conversationID;

    @ManyToOne
    @JoinColumn(name = "cusID", nullable = false)
    private Person customer;

    @OneToMany(mappedBy = "conversation")
    private List<Message> messages;

    @ManyToOne
    @JoinColumn(name = "adminID", nullable = false)
    private Person admin;
}
