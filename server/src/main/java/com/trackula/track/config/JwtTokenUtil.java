package com.trackula.track.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenUtil {
    private String secretKey;
    private long jwtExpiration;
    boolean validate(String token) {
        return true;
    }

    public String getUsername(String token) {
        return "";
    }

    public String generateToken() {
        return "";
    }
}
