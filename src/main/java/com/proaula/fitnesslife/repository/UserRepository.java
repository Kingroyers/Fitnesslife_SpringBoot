package com.proaula.fitnesslife.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.proaula.fitnesslife.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByIdentification(Long identification);
    
    Optional<User> findByQrCodePath(String qrCodePath);
    
    Page<User> findAll(Pageable pageable);
    
    Page<User> findByRole(String role, Pageable pageable);
    
    Page<User> findByIsActive(boolean isActive, Pageable pageable);
    
    Page<User> findByRoleAndIsActive(String role, boolean isActive, Pageable pageable);
    
    @Query("{ $or: [ " +
           "{ 'name': { $regex: ?0, $options: 'i' } }, " +
           "{ 'lastname': { $regex: ?0, $options: 'i' } }, " +
           "{ 'email': { $regex: ?0, $options: 'i' } }, " +
           "{ 'identification': ?1 } " +
           "] }")
    Page<User> searchUsers(String searchTerm, Long identification, Pageable pageable);
    
    @Query("{ 'role': ?0, $or: [ " +
           "{ 'name': { $regex: ?1, $options: 'i' } }, " +
           "{ 'lastname': { $regex: ?1, $options: 'i' } }, " +
           "{ 'email': { $regex: ?1, $options: 'i' } }, " +
           "{ 'identification': ?2 } " +
           "] }")
    Page<User> searchUsersByRole(String role, String searchTerm, Long identification, Pageable pageable);
    
    @Query("{ 'isActive': ?0, $or: [ " +
           "{ 'name': { $regex: ?1, $options: 'i' } }, " +
           "{ 'lastname': { $regex: ?1, $options: 'i' } }, " +
           "{ 'email': { $regex: ?1, $options: 'i' } }, " +
           "{ 'identification': ?2 } " +
           "] }")
    Page<User> searchUsersByStatus(boolean isActive, String searchTerm, Long identification, Pageable pageable);
    
    @Query("{ 'role': ?0, 'isActive': ?1, $or: [ " +
           "{ 'name': { $regex: ?2, $options: 'i' } }, " +
           "{ 'lastname': { $regex: ?2, $options: 'i' } }, " +
           "{ 'email': { $regex: ?2, $options: 'i' } }, " +
           "{ 'identification': ?3 } " +
           "] }")
    Page<User> searchUsersByRoleAndStatus(String role, boolean isActive, String searchTerm, Long identification, Pageable pageable);
}