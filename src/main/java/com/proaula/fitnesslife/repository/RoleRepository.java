package com.proaula.fitnesslife.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proaula.fitnesslife.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

     Role findByNombre(String nombre);
}
