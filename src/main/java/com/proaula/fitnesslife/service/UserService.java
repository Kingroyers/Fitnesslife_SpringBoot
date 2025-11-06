package com.proaula.fitnesslife.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.proaula.fitnesslife.model.User;
import com.proaula.fitnesslife.repository.UserRepository;
import com.proaula.fitnesslife.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        log.debug("Buscando usuario con email: {}", email);
        return userRepository.findByEmail(email);
    }

    public User getUserOrThrow(String email) {
        return findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + email));
    }
}