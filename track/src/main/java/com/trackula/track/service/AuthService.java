package com.trackula.track.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (
                auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_admin"))
        );
    }
}
