package com.luka.userauth.security;

import com.luka.userauth.entity.Role;
import com.luka.userauth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JWTUtil {

    private final String JWT_SECRET = "mySecretJwT12987rw890atb456ksk01";
    private final long VALID_FOR_MILISECONDS = 1000*60*60;
    private final String ISSUER_NAME = "user-auth-service";
    private SecretKey key;

    @PostConstruct
    public void onInit() {
        this.key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }
    //Generate and return JWT token
    public String generateToken(User user){
        //Extract role names from user
        Set<String> userRolesSet = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        return Jwts.builder()
                .claim("roles", userRolesSet)
                .subject(String.valueOf(user.getId()))
                .issuer(ISSUER_NAME)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + VALID_FOR_MILISECONDS))
                .signWith(key)
                .compact();
    }

    //Extract userId
    public Long extractUserId(String token){
        String userId = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.parseLong(userId);
    }

    //Extract Expiration Date
    public Date extractExpirationDate(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    //Validate token
    public boolean isTokenValid(String token){
        try {
            if (extractExpirationDate(token).before(new Date())) throw new JwtException("Token expired.");
            return true;
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    //Extract roles
    public Set<String> extractRoles(String token){
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        List<String> roles = claims.get("roles", List.class);
        return new HashSet<>(roles);
    }

}
