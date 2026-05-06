package com.luka.userauth.service;

import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDtoService;
import com.luka.userauth.dto.RegisterDto;

public interface AuthService {

//    registration
    public String register(RegisterDto registerDto);
//    verification
//    login
    public LoginResponseDtoService login(LoginDto loginDto);
//    logout
//    jwt refresh

}
