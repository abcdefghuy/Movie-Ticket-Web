package com.example.movieticketWeb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Message")
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageID;

    @ManyToOne
    @JoinColumn(name = "conversationID")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "senderID", nullable = false)
    private Person person;

    @Column(name = "content",nullable = false, columnDefinition = "NVARCHAR(1000)")
    private String content;

    @Column(name = "date",nullable = false)
    private Date date;

}