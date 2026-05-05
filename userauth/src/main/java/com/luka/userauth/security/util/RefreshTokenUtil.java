package com.luka.userauth.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenUtil {

    private final boolean IS_PRODUCTION = false;

    private final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60;

    public void addRefreshToken(HttpServletResponse resp, String token){
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(IS_PRODUCTION);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(COOKIE_MAX_AGE);

        resp.addCookie(cookie);
    }

    private String extractFromCookie(HttpServletRequest req){
        if(req.getCookies() == null) return null;

        for(Cookie cookie : req.getCookies()) {
            if("refreshToken".equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }

}
