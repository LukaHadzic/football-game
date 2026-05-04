package com.luka.userauth.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDto {

    @NotBlank
    private String nickOrEmail;
    @NotBlank
    private String password;

    public LoginDto() {
    }

    public LoginDto(String nickOrEmail, String password) {
        this.nickOrEmail = nickOrEmail;
        this.password = password;
    }

    public String getNickOrEmail() {
        return nickOrEmail;
    }

    public void setNickOrEmail(String nickOrEmail) {
        this.nickOrEmail = nickOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
