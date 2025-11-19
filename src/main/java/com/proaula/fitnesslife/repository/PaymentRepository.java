package com.proaula.fitnesslife.repository;

import com.proaula.fitnesslife.model.Payment;
import com.proaula.fitnesslife.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    Page<Payment> findAll(Pageable pageable);
    
    Page<Payment> findByStatus(String status, Pageable pageable);
    
    @Query("{ $or: [ " +
           "{ 'user.name': { $regex: ?0, $options: 'i' } }, " +
           "{ 'user.lastname': { $regex: ?0, $options: 'i' } }, " +
           "{ 'user.email': { $regex: ?0, $options: 'i' } }, " +
           "{ 'transactionId': { $regex: ?0, $options: 'i' } }, " +
           "{ 'externalInvoice': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<Payment> searchPayments(String searchTerm, Pageable pageable);
    
    @Query("{ 'status': ?0, $or: [ " +
           "{ 'user.name': { $regex: ?1, $options: 'i' } }, " +
           "{ 'user.lastname': { $regex: ?1, $options: 'i' } }, " +
           "{ 'user.email': { $regex: ?1, $options: 'i' } }, " +
           "{ 'transactionId': { $regex: ?1, $options: 'i' } }, " +
           "{ 'externalInvoice': { $regex: ?1, $options: 'i' } } " +
           "] }")
    Page<Payment> searchPaymentsByStatus(String status, String searchTerm, Pageable pageable);
}