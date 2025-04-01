package com.apps.biteandsip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class RegisterRequestDTO {
    @NotBlank(message="Username should not be empty") private String username;
    @NotBlank(message="Password should not be empty") private String password;
    @NotBlank(message="First name should not be empty") private String firstName;
    @NotBlank(message="Last name should not be empty") private String lastName;
    @NotBlank(message="Phone number should not be empty") private String phoneNumber;
    private Long userType;

    public RegisterRequestDTO(String username, String password, String firstName, String lastName, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getUserType() {
        return userType;
    }

    public void setUserType(Long userType) {
        this.userType = userType;
    }

    public @NotBlank(message = "Phone number should not be empty") String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NotBlank(message = "Phone number should not be empty") String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
