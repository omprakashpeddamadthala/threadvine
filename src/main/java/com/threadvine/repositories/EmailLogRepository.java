package com.threadvine.repositories;

import com.threadvine.model.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, UUID> {
    List<EmailLog> findByEmailType(EmailLog.EmailType emailType);
    List<EmailLog> findByRecipient(String recipient);
}