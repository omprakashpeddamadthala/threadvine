package com.threadvine.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLog extends BaseEntity {
    
    @Enumerated(EnumType.STRING)
    private EmailType emailType;
    
    private String recipient;
    private String subject;
    
    @Column(length = 1000)
    private String content;
    
    private boolean sent;
    private LocalDateTime sentAt;
    
    @Column(length = 1000)
    private String errorMessage;
    
    public enum EmailType {
        REGISTRATION_EMAIL,
        FORGOT_PASSWORD_EMAIL,
        ORDER_CONFIRMATION_EMAIL,
        ERROR_EMAIL
    }
}