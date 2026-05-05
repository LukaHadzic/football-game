package com.luka.userauth.exception;

import com.luka.userauth.exception.exceptionclasses.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> userAlreadyExists(UserAlreadyExistsException userAlreadyExistsException) {
        return new ResponseEntity<>(userAlreadyExistsException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<String> registrationFailed(RegistrationFailedException registrationFailedException) {
        return new ResponseEntity<>(registrationFailedException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TokenNotValidException.class)
    public ResponseEntity<String> tokenInvalid(RegistrationFailedException registrationFailedException) {
        return new ResponseEntity<>(registrationFailedException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(VerificationFailedException.class)
    public ResponseEntity<String> verificationFailed(VerificationFailedException verificationFailedException) {
        return new ResponseEntity<>(verificationFailedException.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(JWTInvalidException.class)
    public ResponseEntity<String> jwtInvalidToken(JWTInvalidException jWTInvalidException) {
        return new ResponseEntity<>(jWTInvalidException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> jwtInvalidToken(UserNotFoundException userNotFoundException) {
        return new ResponseEntity<>(userNotFoundException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<String> jwtInvalidToken(RefreshTokenException refreshTokenException) {
        return new ResponseEntity<>(refreshTokenException.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
