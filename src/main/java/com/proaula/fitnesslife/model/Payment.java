package com.proaula.fitnesslife.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    @DBRef
    private Plan plan;
    
    private String externalInvoice;
    private Double amount;
    private String currency;
    private String status;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    
    private String transactionId;
    private String approvalCode;
    private String bankName;
    private String franchise;
    private String responseCode;
    private String responseText;
    private String responseReason;
    private String signature;
    
    @CreatedDate
    private LocalDateTime createdAt;
    private LocalDateTime transactionDate;
    
    public boolean isActive() {
        if (validFrom == null || validUntil == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return "ACCEPTED".equals(status) && 
               now.isAfter(validFrom) && 
               now.isBefore(validUntil);
    }
}