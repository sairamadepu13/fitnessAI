package com.fitness.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {

    @JsonIgnore
    private String id;
    private String email;
    @JsonIgnore
    private String password;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    @JsonIgnore
    private LocalDateTime updatedAt;

}
