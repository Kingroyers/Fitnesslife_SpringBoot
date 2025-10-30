package com.proaula.fitnesslife.service;

import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.repository.UserRepository;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Buscando usuario con email: " + email);
        User user = userRepo.findByEmail(email)
        
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
 
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail()) // El username será el email
                .password(user.getPassword())
                .roles(user.getRol()) // O puedes dejar .roles("USER") si solo tienes uno
                .build();

                
    }

}
