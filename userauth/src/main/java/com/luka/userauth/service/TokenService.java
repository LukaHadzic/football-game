package com.luka.userauth.service;

import com.luka.userauth.entity.EmailVerificationToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.repository.EmailVerificationTokenRepository;

import java.util.Optional;

public interface TokenService {

    public EmailVerificationToken generateToken(User user);

    public void saveToken(EmailVerificationToken token);

}
