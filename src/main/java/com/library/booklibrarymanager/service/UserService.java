package com.library.booklibrarymanager.service;

import com.library.booklibrarymanager.entity.User;
import com.library.booklibrarymanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElse(null); 
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}