package com.luka.userauth.controller;

import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDtoController;
import com.luka.userauth.dto.LoginResponseDtoService;
import com.luka.userauth.dto.RegisterDto;
import com.luka.userauth.entity.RefreshToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.security.util.RefreshTokenUtil;
import com.luka.userauth.service.*;
import jakarta.servlet.http.HttpServletRequest;
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
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, TokenService tokenService, VerificationService verificationService, LogoutService logoutService, RefreshTokenUtil refreshTokenUtil, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.verificationService = verificationService;
        this.logoutService = logoutService;
        this.refreshTokenUtil = refreshTokenUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto request){
        return new ResponseEntity<>(authService.register(request), HttpStatus.OK);
    }

    @GetMapping("/validate-email")
    public ResponseEntity<String> verifyEMail(@RequestParam String token){

        User user = verificationService.verifyUser(token);

        RefreshToken refreshToken = refreshTokenService.create(user);

        return new ResponseEntity<>("Email successfully verified.", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDtoController> login(@Valid @RequestBody LoginDto request, HttpServletResponse resp){

        LoginResponseDtoService serviceResp = authService.login(request);

        refreshTokenUtil.addRefreshToken(resp, serviceResp.getRefreshToken());

        return new ResponseEntity<>(new LoginResponseDtoController(serviceResp.getAccessToken(), serviceResp.getUserDto()), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest req, HttpServletResponse resp){
        authService.logout(refreshTokenUtil.extractFromCookie(req));
        refreshTokenUtil.deleteRefreshToken(resp);
        return new ResponseEntity<>("Logout successful.", HttpStatus.OK);
    }

    // DODATI FLYWAY SKRIPTE ZA REFRESHTOKEN-e -> napraviti tabelu i relacije koje treba

}
