package com.threadvine.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendGridClient {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name}")
    private String fromName;

    public void sendEmail(String to, String subject, String content) {
        try {
            SendGrid sg = new SendGrid( apiKey );

            Email from = new Email( fromEmail, fromName );
            Email toEmail = new Email( to );
            Content emailContent = new Content( "text/html", content );
            Mail mail = new Mail( from, subject, toEmail, emailContent );

            Request request = new Request();
            request.setMethod( Method.POST );
            request.setEndpoint( "mail/send" );
            request.setBody( mail.build() );

            Response response = sg.api( request );
            log.info( "Email sent to {} with status code: {}", to, response.getStatusCode() );
        } catch (IOException e) {
            log.error( "Failed to send email to {}: {}", to, e.getMessage() );
            throw new RuntimeException( "Failed to send email", e );
        }
    }
}