package com.luka.userauth.service.impl;

import com.luka.userauth.entity.EmailVerificationToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.repository.EmailVerificationTokenRepository;
import com.luka.userauth.service.TokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    private final EmailVerificationTokenRepository emailVerifTokenRepository;
    private final long VERIF_TOKEN_VALID_FOR = 1;

    public TokenServiceImpl(EmailVerificationTokenRepository emailVerifTokenRepository) {
        this.emailVerifTokenRepository = emailVerifTokenRepository;
    }

    @Override
    public EmailVerificationToken generateToken(User user) {
        //Generate empty token object
        EmailVerificationToken tokenObj = new EmailVerificationToken();
        //Fill data into token object
        tokenObj.setToken(UUID.randomUUID().toString());
        tokenObj.setUser(user);
        tokenObj.setCreatedAt(LocalDateTime.now());
        tokenObj.setExpiresAt(LocalDateTime.now().plusDays(VERIF_TOKEN_VALID_FOR));
        tokenObj.setUsed(false);

        //Return token
        return tokenObj;
    }

    @Override
    public void saveToken(EmailVerificationToken token) {
        //Save token object into db
        emailVerifTokenRepository.save(token);
    }

}
