package com.proaula.fitnesslife.repository;

import com.proaula.fitnesslife.model.Access;
import com.proaula.fitnesslife.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessRepository extends MongoRepository<Access, String> {
    
    List<Access> findByUser(User user);
    
    List<Access> findByUserOrderByAccessedAtDesc(User user);
    
    List<Access> findByResult(String result);
    
    List<Access> findByAccessedAtBetween(LocalDateTime start, LocalDateTime end);
    
    long countByAccessedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Access> findTopNByOrderByAccessedAtDesc(int limit);
    
    Page<Access> findAll(Pageable pageable);
    
    Page<Access> findByResult(String result, Pageable pageable);
    
    @Query("{ $or: [ " +
           "{ 'user.name': { $regex: ?0, $options: 'i' } }, " +
           "{ 'user.lastname': { $regex: ?0, $options: 'i' } }, " +
           "{ 'user.email': { $regex: ?0, $options: 'i' } }, " +
           "{ 'qrCode': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<Access> searchAccesses(String searchTerm, Pageable pageable);
    
    @Query("{ 'result': ?0, $or: [ " +
           "{ 'user.name': { $regex: ?1, $options: 'i' } }, " +
           "{ 'user.lastname': { $regex: ?1, $options: 'i' } }, " +
           "{ 'user.email': { $regex: ?1, $options: 'i' } }, " +
           "{ 'qrCode': { $regex: ?1, $options: 'i' } } " +
           "] }")
    Page<Access> searchAccessesByResult(String result, String searchTerm, Pageable pageable);
}