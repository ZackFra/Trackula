package com.trackula.track.controller;

import com.trackula.track.dto.CreateUserRequest;
import com.trackula.track.dto.GetUserResponse;
import com.trackula.track.repository.UsersRepository;
import com.trackula.track.service.AuthService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final UsersRepository usersRepository;

    public UserController(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, AuthService authService, UsersRepository usersRepository) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.usersRepository = usersRepository;
    }

    @GetMapping("/{username}")
    public ResponseEntity<GetUserResponse> getUserByUsername(@PathVariable String username, Principal principal) {
        UserDetails foundUser;
        if(authService.isAdmin() || principal.getName().equals(username)) {
            foundUser = userDetailsManager.loadUserByUsername(username);
        } else {
            return ResponseEntity.notFound().build();
        }

        GetUserResponse response = new GetUserResponse();
        response.setUsername(foundUser.getUsername());
        response.setRole(foundUser.getAuthorities().iterator().next().getAuthority());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping
    public ResponseEntity<List<GetUserResponse>> getAllUsers() {
        Iterator<com.trackula.track.model.User> usersIterable = usersRepository.findAllWithAuthorities().iterator();
        List<GetUserResponse> users = new ArrayList<>();
        while(usersIterable.hasNext()) {
            com.trackula.track.model.User user = usersIterable.next();
            GetUserResponse userResponse = new GetUserResponse();
            //userResponse.setRole(user);
        }
        return ResponseEntity.internalServerError().build();

    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @PutMapping
    public ResponseEntity<Void> createUser(@RequestBody CreateUserRequest createUserRequest) {
        String username = createUserRequest.getUsername();
        String password = createUserRequest.getPassword();
        String role = createUserRequest.getRole();
        if(username == null || password == null || role == null) {
            return ResponseEntity.badRequest().build();
        }

        if(userDetailsManager.userExists(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserDetails admin = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(role)
                .build();
        userDetailsManager.createUser(admin);
        URI uri = URI.create("/user/" + username);
        return ResponseEntity.created(uri).build();
    }
}
