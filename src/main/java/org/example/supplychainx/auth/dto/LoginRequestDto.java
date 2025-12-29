package org.example.supplychainx.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;



 public class LoginRequestDto {

    @Email(message = "Please provide a valid email address.")
    @NotBlank(message = "Email must not be blank.")
    private String email;

    @NotBlank(message = "Password must not be blank.")
    private String password;

     public LoginRequestDto() { }

    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}