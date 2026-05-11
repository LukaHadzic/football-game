package com.luka.userauth.controller;

import com.luka.userauth.dto.*;
import com.luka.userauth.entity.RefreshToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.security.util.JWTUtil;
import com.luka.userauth.security.util.RefreshTokenUtil;
import com.luka.userauth.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpRequest;
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
    private final JWTUtil jwtUtil;

    public AuthController(AuthService authService, TokenService tokenService, VerificationService verificationService, LogoutService logoutService, RefreshTokenUtil refreshTokenUtil, RefreshTokenService refreshTokenService, JWTUtil jwtUtil) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.verificationService = verificationService;
        this.logoutService = logoutService;
        this.refreshTokenUtil = refreshTokenUtil;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
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

    @PostMapping("/refresh")
    public ResponseEntity<RefreshDto> refresh(HttpServletRequest req, HttpServletResponse resp){

        RefreshToken newToken = refreshTokenService.rotate(refreshTokenUtil.extractFromCookie(req));
        refreshTokenUtil.addRefreshToken(resp, newToken.getToken());

        return new ResponseEntity<>(new RefreshDto(jwtUtil.generateToken(newToken.getUser())), HttpStatus.OK);
    }


}
