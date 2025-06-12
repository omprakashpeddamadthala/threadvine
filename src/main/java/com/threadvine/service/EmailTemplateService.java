package com.threadvine.service;

import com.threadvine.dto.OrderDTO;
import com.threadvine.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {
    
    public String buildRegistrationEmailTemplate(User user, String temporaryPassword) {
        return "<html><body>" +
               "<h1>Welcome to ThreadVine!</h1>" +
               "<p>Your account has been successfully created.</p>" +
               "<p>Please login with the following credentials:</p>" +
               "<p>Username: " + user.getEmail() + "</p>" +
               "<p>Temporary Password: " + temporaryPassword + "</p>" +
               "<p>Please change your password after logging in.</p>" +
               "<a href='http://threadvine.com/login'>Login Now</a>" +
               "</body></html>";
    }
    
    public String buildPasswordResetEmailTemplate(User user, String resetToken) {
        return "<html><body>" +
               "<h1>Password Reset Request</h1>" +
               "<p>We received a request to reset your password.</p>" +
               "<p>Click the link below to reset your password:</p>" +
               "<a href='http://threadvine.com/reset-password?token=" + resetToken + "'>Reset Password</a>" +
               "<p>If you didn't request this, please ignore this email.</p>" +
               "</body></html>";
    }
    
    public String buildOrderConfirmationEmailTemplate(OrderDTO orderDTO) {
        StringBuilder itemsHtml = new StringBuilder();
        double total = 0;
        
        for (var item : orderDTO.getItems()) {
            double itemTotal = Double.valueOf( String.valueOf( item.getPrice().multiply(  BigDecimal.valueOf( item.getQuantity() ) ) ) );
            total += itemTotal;
            
            itemsHtml.append("<tr>")
                    .append("<td>").append(item.getQuantity()).append("</td>")
                    .append("<td>$").append(String.format("%.2f", item.getPrice())).append("</td>")
                    .append("<td>$").append(String.format("%.2f", itemTotal)).append("</td>")
                    .append("</tr>");
        }
        
        return "<html><body>" +
               "<h1>Order Confirmation</h1>" +
               "<p>Thank you for your order!</p>" +
               "<p>Order ID: " + orderDTO.getId() + "</p>" +
               "<p>Order Date: " + orderDTO.getCreatedAt() + "</p>" +
               "<p>Shipping Address: " + orderDTO.getAddress() + "</p>" +
               "<h2>Order Items:</h2>" +
               "<table border='1' cellpadding='5'>" +
               "<tr><th>Product</th><th>Quantity</th><th>Price</th><th>Total</th></tr>" +
               itemsHtml.toString() +
               "<tr><td colspan='3'><strong>Total</strong></td><td>$" + String.format("%.2f", total) + "</td></tr>" +
               "</table>" +
               "</body></html>";
    }
    
    public String buildErrorEmailTemplate(Exception exception, String context) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();
        
        return "<html><body>" +
               "<h1>System Error Notification</h1>" +
               "<p><strong>Error Context:</strong> " + context + "</p>" +
               "<p><strong>Error Message:</strong> " + exception.getMessage() + "</p>" +
               "<p><strong>Stack Trace:</strong></p>" +
               "<pre>" + stackTrace + "</pre>" +
               "</body></html>";
    }
}