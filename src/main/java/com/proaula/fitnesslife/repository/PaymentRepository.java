package com.proaula.fitnesslife.repository;

import com.proaula.fitnesslife.model.Payment;
import com.proaula.fitnesslife.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    
    Optional<Payment> findByExternalInvoice(String externalInvoice);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByUser(User user);

    List<Payment> findByUserOrderByCreatedAtDesc(User user);
    
    List<Payment> findByUserAndStatus(User user, String status);
    
    List<Payment> findByUserAndStatusAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
        User user, 
        String status, 
        LocalDateTime validFrom, 
        LocalDateTime validUntil
    );
    
    Optional<Payment> findFirstByUserAndStatusOrderByCreatedAtDesc(User user, String status);
    
    @Query("{ 'status': 'ACCEPTED', 'validUntil': { $lt: ?0 } }")
    List<Payment> findExpiredPayments(LocalDateTime now);
}