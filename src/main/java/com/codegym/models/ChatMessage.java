package com.codegym.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;   // username gửi
    private String receiver;// username nhận
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime timestamp;

}
