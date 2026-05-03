package com.luka.userauth.exception;

import com.luka.userauth.exception.exceptionclasses.RegistrationFailedException;
import com.luka.userauth.exception.exceptionclasses.TokenNotValidException;
import com.luka.userauth.exception.exceptionclasses.UserAlreadyExistsException;
import com.luka.userauth.exception.exceptionclasses.VerificationFailedException;
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

}
