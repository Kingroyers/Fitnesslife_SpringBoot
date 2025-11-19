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
@Document(collection = "accesses")
public class Access {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    private String qrCode;
    
    @CreatedDate
    private LocalDateTime accessedAt;
    
    // ALLOWED, DENIED
    private String result;
}