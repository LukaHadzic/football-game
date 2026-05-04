package com.luka.userauth.service.impl;

import com.luka.userauth.dto.LoginDto;
import com.luka.userauth.dto.LoginResponseDto;
import com.luka.userauth.dto.RegisterDto;
import com.luka.userauth.entity.EmailVerificationToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.RegistrationFailedException;
import com.luka.userauth.exception.exceptionclasses.UserAlreadyExistsException;
import com.luka.userauth.exception.exceptionclasses.UserNotFoundException;
import com.luka.userauth.mapper.UserMapper;
import com.luka.userauth.repository.UserRepository;
import com.luka.userauth.security.JWTUtil;
import com.luka.userauth.service.AuthService;
import com.luka.userauth.service.NotificationService;
import com.luka.userauth.service.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final TransactionTemplate transactionTemplate;
    private final NotificationService notificationService;
    private final JWTUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, TokenService tokenService, TransactionTemplate transactionTemplate, NotificationService notificationService, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.tokenService = tokenService;
        this.transactionTemplate = transactionTemplate;
        this.notificationService = notificationService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String register(RegisterDto registerDto) {
        //Check if user already exists
        Optional<User> u1 = userRepository.findByEmail(registerDto.getEmail());
        if(u1.isPresent()) throw new UserAlreadyExistsException("Cannot finish registration - User already exists.");

        //Create entity from request data
        User user = userMapper.registerToEntity(registerDto);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setVerified(false);

        //Call TokenService to generate token
        EmailVerificationToken generatedToken = tokenService.generateToken(user);

        //Open transaction to save User and token
        try {
            transactionTemplate.execute(status -> {
                saveTokenAndUser(user, generatedToken);
                return null;
            });
        }catch(Exception e) {
            throw new RegistrationFailedException("Registration failed, please try again later.");
        }

        //Call NotificationService to send email to provided email address
        notificationService.sendVerificationEmail(user.getEmail(), generatedToken.getToken());

        return "Check provided email's inbox in order to verify Your identity.";
    }

    protected void saveTokenAndUser(User user, EmailVerificationToken emailVerificationToken) {
        userRepository.save(user);
        tokenService.saveToken(emailVerificationToken);
    }

    public LoginResponseDto login(LoginDto loginDto){
        String nickOrEmail = loginDto.getNickOrEmail();

        User user = userRepository.findByEmailOrNick(nickOrEmail)
                .orElseThrow(() -> new UserNotFoundException("Wrong login credentials."));

        if(!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())){
            throw new UserNotFoundException("Wrong login credentials.");
        }

        String token = jwtUtil.generateToken(user);

        return new LoginResponseDto(token, userMapper.toUserDto(user));


    }

}
























