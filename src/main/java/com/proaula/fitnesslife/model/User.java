package com.proaula.fitnesslife.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    private Long identification;
    private String name;
    private String lastname;

    @Indexed(unique = true)  
    private String email;
  
    private String phone;
    private String password;
    private String photoProfile;
    private String sex;
    private String bloodType;
    private boolean isActive;
    private String rol;
    private String plan; 
    private LocalDateTime createdAt;
    private LocalDateTime lastlogin;

    public User() {}

    public User(String id, Long identification, String name, String lastname, String email, LocalDateTime createdAt,
            String phone, String password, String photoProfile, LocalDateTime lastlogin, String bloodType,
            boolean isActive, String rol, String plan, String sex) {
        this.id = id;
        this.identification = identification;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.createdAt =  LocalDateTime.now();
        this.phone = phone;
        this.password = password;
        this.photoProfile = photoProfile;
        this.lastlogin = lastlogin;
        this.bloodType = bloodType;
        this.isActive = true;
        this.rol = "USER";
        this.plan = plan;
        this.sex = sex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getIdentification() {
        return identification;
    }

    public void setIdentification(Long identification) {
        this.identification = identification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(String photoProfile) {
        this.photoProfile = photoProfile;
    }

    public LocalDateTime getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(LocalDateTime lastlogin) {
        this.lastlogin = lastlogin;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    

       
}

