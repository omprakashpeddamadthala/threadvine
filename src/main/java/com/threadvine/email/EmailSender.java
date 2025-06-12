package com.threadvine.email;

import com.threadvine.model.EmailLog;

public interface EmailSender {
    void send(String to, String subject, String content);
    EmailLog.EmailType getEmailType();
}