package com.cooksys.groupfinal.services.impl;

import com.cooksys.groupfinal.entities.User;
import com.cooksys.groupfinal.repositories.UserRepository;
import com.cooksys.groupfinal.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    private final UserRepository userRepository;

    @Override
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByCredentialsUsername(username);
    }
    @Override
    public boolean usernameExists(String username) {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if (user.getCredentials().getUsername().equals(username)) {
                return true;}
        }
        return false;
    }
}
