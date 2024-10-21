package com.trackula.track.controller;

import com.trackula.track.dto.CreateUserRequest;
import com.trackula.track.dto.GetUserResponse;
import com.trackula.track.model.Authorities;
import com.trackula.track.repository.AuthoritiesRepository;
import com.trackula.track.repository.UsersRepository;
import com.trackula.track.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final UsersRepository usersRepository;
    private final AuthoritiesRepository authoritiesRepository;

    public UserController(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, AuthService authService, UsersRepository usersRepository, AuthoritiesRepository authoritiesRepository) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.usersRepository = usersRepository;
        this.authoritiesRepository = authoritiesRepository;
    }

    @GetMapping("/{username}")
    public ResponseEntity<GetUserResponse> getUserByUsername(@PathVariable String username, Principal principal) {
        UserDetails foundUser;
        if(authService.isAdmin() || principal.getName().equals(username)) {
            foundUser = userDetailsManager.loadUserByUsername(username);
        } else {
            return ResponseEntity.notFound().build();
        }

        Iterable<Authorities> authorities = authoritiesRepository.findAllByUsername(username);
        ArrayList<String> roles = new ArrayList<>();
        for(Authorities authority : authorities) {
            roles.add(authority.authority());
        }

        GetUserResponse response = new GetUserResponse();
        response.setUsername(foundUser.getUsername());
        response.setRoles(roles);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping
    public ResponseEntity<List<GetUserResponse>> getAllUsers() {
        Iterable<com.trackula.track.model.User> usersIterable = usersRepository.findAll();
        List<com.trackula.track.model.User> users = StreamSupport.stream(usersIterable.spliterator(), false)
                .toList();
        Map<String, GetUserResponse> userResponseByUsername = new HashMap<>();
        for(com.trackula.track.model.User user : users) {
            GetUserResponse response = new GetUserResponse();
            response.setUsername(user.username());
            userResponseByUsername.put(user.username(), response);
        }
        Iterable<Authorities> authoritiesIterable = authoritiesRepository.findAllByUsernames(userResponseByUsername.keySet().stream().toList());
        List<Authorities> authorities = StreamSupport.stream(authoritiesIterable.spliterator(), false)
                .toList();
        for(Authorities authority : authorities) {
            GetUserResponse response = userResponseByUsername.get(authority.username());
            List<String> roles = response.getRoles();
            if(roles == null) {
                roles = new ArrayList<>();
            }
            roles.add(authority.authority());
            response.setRoles(roles);
        }

        ArrayList<GetUserResponse> responses = new ArrayList<>(userResponseByUsername.values());

        return ResponseEntity.ok(responses);

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
