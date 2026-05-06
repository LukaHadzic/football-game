package com.luka.userauth.service;

import com.luka.userauth.entity.RefreshToken;
import com.luka.userauth.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface RefreshTokenService {

    RefreshToken create(User user);

    RefreshToken validate(String token);

    RefreshToken validate(User user);

    RefreshToken rotate(RefreshToken oldToken);

    void revoke(String token);

}
