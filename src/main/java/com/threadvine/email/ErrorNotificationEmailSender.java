package com.threadvine.email;

import com.threadvine.model.EmailLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErrorNotificationEmailSender implements EmailSender {
    private final SendGridClient sendGridClient;
    
    @Override
    public void send(String to, String subject, String content) {
        sendGridClient.sendEmail(to, subject, content);
    }
    
    @Override
    public EmailLog.EmailType getEmailType() {
        return EmailLog.EmailType.ERROR_EMAIL;
    }
}