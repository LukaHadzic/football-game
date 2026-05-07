package com.luka.userauth.service;

import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDtoService;
import com.luka.userauth.dto.RegisterDto;

public interface AuthService {

//    registration
    public String register(RegisterDto registerDto);
//    verification -> U VerificationService
//    login -> ZAVRSENO
    public LoginResponseDtoService login(LoginDto loginDto);
//    logout
    public void logout(String token);
//    jwt refresh

}
