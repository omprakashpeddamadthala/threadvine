package com.threadvine.service.impl;

import com.threadvine.dto.OrderDTO;
import com.threadvine.email.EmailSender;
import com.threadvine.exceptions.UserNotFoundException;
import com.threadvine.model.EmailLog;
import com.threadvine.model.User;
import com.threadvine.repositories.EmailLogRepository;
import com.threadvine.repositories.UserRepository;
import com.threadvine.service.EmailService;
import com.threadvine.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    
    private final EmailLogRepository emailLogRepository;
    private final EmailTemplateService templateService;
    private final List<EmailSender> emailSenders;
    private final UserRepository userRepository;
    
    @Value("${admin.email}")
    private String adminEmail;
    
    @Async
    @Override
    public void sendRegistrationEmail(User user, String temporaryPassword) {
        try {
            String subject = "Welcome to ThreadVine - Account Registration";
            String content = templateService.buildRegistrationEmailTemplate(user, temporaryPassword);
            
            EmailSender sender = getEmailSender( EmailLog.EmailType.REGISTRATION_EMAIL);
            sender.send(user.getEmail(), subject, content);
            
            logEmail(EmailLog.builder()
                    .emailType(EmailLog.EmailType.REGISTRATION_EMAIL)
                    .recipient(user.getEmail())
                    .subject(subject)
                    .content(content)
                    .sent(true)
                    .sentAt( LocalDateTime.now())
                    .build());
                    
        } catch (Exception e) {
            log.error("Failed to send registration email to {}: {}", user.getEmail(), e.getMessage());
            logEmail(EmailLog.builder()
                    .emailType(EmailLog.EmailType.REGISTRATION_EMAIL)
                    .recipient(user.getEmail())
                    .subject("Welcome to ThreadVine - Account Registration")
                    .sent(false)
                    .errorMessage(e.getMessage())
                    .build());
            
            sendErrorEmail(e, "Failed to send registration email to " + user.getEmail());
        }
    }
    
    @Async
    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            String subject = "ThreadVine - Password Reset Request";
            String content = templateService.buildPasswordResetEmailTemplate(user, resetToken);
            
            EmailSender sender = getEmailSender(EmailLog.EmailType.FORGOT_PASSWORD_EMAIL);
            sender.send(user.getEmail(), subject, content);
            
            logEmail(EmailLog.builder()
                    .emailType(EmailLog.EmailType.FORGOT_PASSWORD_EMAIL)
                    .recipient(user.getEmail())
                    .subject(subject)
                    .content(content)
                    .sent(true)
                    .sentAt(LocalDateTime.now())
                    .build());
                    
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", user.getEmail(), e.getMessage());
            logEmail(EmailLog.builder()
                    .emailType(EmailLog.EmailType.FORGOT_PASSWORD_EMAIL)
                    .recipient(user.getEmail())
                    .subject("ThreadVine - Password Reset Request")
                    .sent(false)
                    .errorMessage(e.getMessage())
                    .build());
            
            sendErrorEmail(e, "Failed to send password reset email to " + user.getEmail());
        }
    }
    
    @Async
    @Override
    public void sendOrderConfirmationEmail(OrderDTO orderDTO) {
        try {
            UUID userId =  orderDTO.getUserId();
            String userEmail = this.getUserByUserId(  userId).getEmail();
            String subject = "ThreadVine - Order Confirmation #" + orderDTO.getId();
            String content = templateService.buildOrderConfirmationEmailTemplate(orderDTO);
            
            EmailSender sender = getEmailSender(EmailLog.EmailType.ORDER_CONFIRMATION_EMAIL);
            sender.send(userEmail, subject, content);
            
            logEmail(EmailLog.builder()
                    .emailType(EmailLog.EmailType.ORDER_CONFIRMATION_EMAIL)
                    .recipient(userEmail)
                    .subject(subject)
                    .content(content)
                    .sent(true)
                    .sentAt(LocalDateTime.now())
                    .build());
                    
        } catch (Exception e) {
            log.error("Failed to send order confirmation email for order {}: {}", orderDTO.getId(), e.getMessage());
            UUID userId =  orderDTO.getUserId();
            String userEmail = this.getUserByUserId(  userId).getEmail();
            logEmail(EmailLog.builder()
                    .emailType(EmailLog.EmailType.ORDER_CONFIRMATION_EMAIL)
                    .recipient(userEmail)
                    .subject("ThreadVine - Order Confirmation #" + orderDTO.getId())
                    .sent(false)
                    .errorMessage(e.getMessage())
                    .build());
            
            sendErrorEmail(e, "Failed to send order confirmation email for order " + orderDTO.getId());
        }
    }
    
    @Async
    @Override
    public void sendErrorEmail(Exception exception, String context) {
        try {
            String subject = "ThreadVine - System Error Notification";
            String content = templateService.buildErrorEmailTemplate(exception, context);
            
            EmailSender sender = getEmailSender(EmailLog.EmailType.ERROR_EMAIL);
            sender.send(adminEmail, subject, content);
            
            logEmail(EmailLog.builder()
                    .emailType(EmailLog.EmailType.ERROR_EMAIL)
                    .recipient(adminEmail)
                    .subject(subject)
                    .content(content)
                    .sent(true)
                    .sentAt(LocalDateTime.now())
                    .build());
                    
        } catch (Exception e) {
            log.error("Failed to send error notification email: {}", e.getMessage());
            logEmail(EmailLog.builder()
                    .emailType(EmailLog.EmailType.ERROR_EMAIL)
                    .recipient(adminEmail)
                    .subject("ThreadVine - System Error Notification")
                    .sent(false)
                    .errorMessage(e.getMessage())
                    .build());
        }
    }
    
    @Override
    public void logEmail(EmailLog emailLog) {
        try {
            emailLogRepository.save(emailLog);
        } catch (Exception e) {
            log.error("Failed to log email: {}", e.getMessage());
        }
    }
    
    private EmailSender getEmailSender(EmailLog.EmailType emailType) {
        return emailSenders.stream()
                .filter(sender -> sender.getEmailType() == emailType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No email sender found for type: " + emailType));
    }

    private User getUserByUserId(UUID userId) {
        return userRepository.findById( userId ).orElseThrow( () -> new UserNotFoundException( "User not found with id: " + userId ) );
    }

}