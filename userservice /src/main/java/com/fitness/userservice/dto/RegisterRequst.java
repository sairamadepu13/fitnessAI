package com.fitness.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequst {

    @NotBlank(message = "Email is requried")
    @Email(message = "Invalid format")
    private String email;

    @NotBlank(message = "Passwprd is requried")
    @Size(min = 6, message = "Password must requried atleast 6 characters")
    private String password;

    private String firstName;
    private String lastName;
}
