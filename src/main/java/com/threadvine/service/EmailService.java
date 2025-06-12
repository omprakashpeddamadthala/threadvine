package com.threadvine.service;

import com.threadvine.dto.OrderDTO;
import com.threadvine.model.EmailLog;
import com.threadvine.model.User;

public interface EmailService {
    void sendRegistrationEmail(User user, String temporaryPassword);
    void sendPasswordResetEmail(User user, String resetToken);
    void sendOrderConfirmationEmail(OrderDTO orderDTO);
    void sendErrorEmail(Exception exception, String context);
    void logEmail(EmailLog emailLog);
}