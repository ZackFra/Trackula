package com.trackula.track.dto;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class CreateUserRequest {
    private String username;
    private String password;
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        role = role.toLowerCase();
        List<String> validRoles = List.of("admin", "user");
        if(!validRoles.contains(role)) {
            throw new IllegalArgumentException("Role must be user or admin");
        }
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
