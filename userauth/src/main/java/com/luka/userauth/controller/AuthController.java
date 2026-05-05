package com.luka.userauth.controller;

import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDto;
import com.luka.userauth.dto.RegisterDto;
import com.luka.userauth.security.util.RefreshTokenUtil;
import com.luka.userauth.service.AuthService;
import com.luka.userauth.service.LogoutService;
import com.luka.userauth.service.TokenService;
import com.luka.userauth.service.VerificationService;
import jakarta.servlet.http.HttpServletResponse;
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
    private final RefreshTokenUtil refreshTokenUtil;

    public AuthController(AuthService authService, TokenService tokenService, VerificationService verificationService, LogoutService logoutService, RefreshTokenUtil refreshTokenUtil) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.verificationService = verificationService;
        this.logoutService = logoutService;
        this.refreshTokenUtil = refreshTokenUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto request){
        return new ResponseEntity<>(authService.register(request), HttpStatus.OK);
    }

    @GetMapping("/validate-email")
    public ResponseEntity<String> verifyEMail(@RequestParam String token){
        return new ResponseEntity<>(verificationService.verifyUser(token), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto request, HttpServletResponse resp){

        //OVDE IDE LOGIKA ZA KREIRANJE REFRESH TOKENA I SVEGA OSTALOG
            //Treba da se zove authService koji orkestrira kreiranje RefreshTokena u login metodi

        refreshTokenUtil.addRefreshToken(resp, "OVDE TREBA REFRESH TOKEN");

        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }

}
