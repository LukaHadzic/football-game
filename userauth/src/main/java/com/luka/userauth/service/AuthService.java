package com.luka.userauth.service;

import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDto;
import com.luka.userauth.dto.RegisterDto;

public interface AuthService {

//    registration
    public String register(RegisterDto registerDto);
//    verification
//    login
    public LoginResponseDto login(LoginDto loginDto);
//    logout
//    jwt refresh

}
