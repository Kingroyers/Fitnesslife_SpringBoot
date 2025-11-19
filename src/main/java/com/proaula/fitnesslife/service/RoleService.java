package com.proaula.fitnesslife.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proaula.fitnesslife.model.Role;
import com.proaula.fitnesslife.repository.RoleRepository;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role findByNombre(String nombre) {
        return roleRepository.findByNombre(nombre);
    }
}
