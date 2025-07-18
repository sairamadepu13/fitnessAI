package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequst;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public UserResponse getUserProfile(String userId) {
       User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not Found"));
        UserResponse userResponse =  new UserResponse();
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        return userResponse;
    }
    public UserResponse register(@Valid RegisterRequst request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = repository.save(user);

        UserResponse userResponse =  new UserResponse();
        userResponse.setId(savedUser.getId());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        return userResponse;
    }

    public Boolean existByUserId(String userId) {

        log.info("Calling USer Validation API for UserId: {}",userId);
        return repository.existsById(userId);
    }
}
