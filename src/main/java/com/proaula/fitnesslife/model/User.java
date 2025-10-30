package com.proaula.fitnesslife.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    private Long identificacion;
    private String name;
    private String lastname;
    private String email;
    private String password;// 🔒 Nueva propiedad
    private String rol;
    private String Plan; // ID del plan asociado

    public User() {}

    public User(Long identificacion, String name, String lastname, String email, String password, String Plan, String rol) {
         this.rol = rol;
        this.identificacion = identificacion;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.Plan = Plan;
    }


    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(Long identificacion) {
        this.identificacion = identificacion;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getPlan() {
        return Plan;
    }

    public void setPlan(String Plan) {
        this.Plan = Plan;
    }
}

