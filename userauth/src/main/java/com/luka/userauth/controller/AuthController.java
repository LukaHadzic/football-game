package com.luka.userauth.controller;

import com.luka.userauth.dto.RegisterDto;
import com.luka.userauth.service.AuthService;
import com.luka.userauth.service.LogoutService;
import com.luka.userauth.service.TokenService;
import com.luka.userauth.service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final VerificationService verificationService;
    private final LogoutService logoutService;

    public AuthController(AuthService authService, TokenService tokenService, VerificationService verificationService, LogoutService logoutService) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.verificationService = verificationService;
        this.logoutService = logoutService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto request){
        return new ResponseEntity<>(authService.register(request), HttpStatus.OK);
    }

    @GetMapping("/validate-email")
    public ResponseEntity<String> verifyEMail(@RequestParam String token){
        return new ResponseEntity<>(verificationService.verifyUser(token), HttpStatus.OK);
    }

}
