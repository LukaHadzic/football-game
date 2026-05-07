package com.luka.userauth.service.impl;

import com.luka.userauth.entity.RefreshToken;
import com.luka.userauth.entity.User;
import com.luka.userauth.exception.exceptionclasses.RefreshTokenException;
import com.luka.userauth.exception.exceptionclasses.RegistrationFailedException;
import com.luka.userauth.repository.RefreshTokenRepository;
import com.luka.userauth.service.RefreshTokenService;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final long REFRESH_TOKEN_VALID_FOR_DAYS = 7;

    private final RefreshTokenRepository refreshTokenRepository;

    private final TransactionTemplate transactionTemplate;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, TransactionTemplate transactionTemplate) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public RefreshToken create(User user) {

        RefreshToken newToken = new RefreshToken();
        newToken.setUser(user);
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setRevoked(false);
        newToken.setCreatedAt(LocalDateTime.now());
        newToken.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_VALID_FOR_DAYS));

        return refreshTokenRepository.save(newToken);
    }

    @Override
    public RefreshToken validate(String token) {
        RefreshToken dbToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException("Invalid refresh token."));

        if (dbToken.isRevoked() || dbToken.isExpired()) return null;

        return dbToken;

    }

    @Override
    public RefreshToken validateOnLogin(User user){
        Optional<RefreshToken> dbToken = refreshTokenRepository.findByUserAndRevokedFalse(user);

        if (dbToken.isPresent()){
            RefreshToken refreshToken = dbToken.get();
            if (refreshToken.isExpired()){
                revoke(refreshToken.getToken());
                return create(user);
            }
            return refreshToken;
        }

        return create(user);

    }

    @Override
    public RefreshToken rotate(RefreshToken oldToken) {

        RefreshToken newToken = new RefreshToken();
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setRevoked(false);
        newToken.setCreatedAt(LocalDateTime.now());
        newToken.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_VALID_FOR_DAYS));

        try {
            return transactionTemplate.execute(status -> {
                RefreshToken oldDbToken = refreshTokenRepository.findByToken(oldToken.getToken())
                        .orElseThrow(() -> new RefreshTokenException("Refresh token not valid."));

                if(oldDbToken.isRevoked()){
                    throw new RefreshTokenException("Refresh token already revoked.");
                }

                if(oldDbToken.getExpiresAt().isBefore(LocalDateTime.now())){
                    throw new RefreshTokenException("Refresh token expired.");
                }

                newToken.setUser(oldDbToken.getUser());
                revoke(oldToken.getToken());

                refreshTokenRepository.save(oldDbToken);
                return refreshTokenRepository.save(newToken);

            });
        }catch(Exception e) {
            e.printStackTrace();
            throw new RefreshTokenException("Server error - refreshing failed, please try again later.");
        }
    }

    @Transactional
    @Override
    public void revoke(String token) {

            int updated = refreshTokenRepository.revokeByToken(token);

            if(updated == 0){
                throw new RefreshTokenException("Refresh token not valid.");
            }

    }

}
