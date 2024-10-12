package com.trackula.track.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trackula.track.TrackApplication;
import com.trackula.track.dto.CreateUserRequest;
import com.trackula.track.dto.GetUserResponse;
import org.apache.coyote.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.annotation.DirtiesContext;

import static com.trackula.track.controller.TestDataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes= TrackApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    UserDetailsManager userDetailsManager;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void ensureUserCannotCreateUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testuser");
        createUserRequest.setPassword("abc123");
        createUserRequest.setRole("user");
        ResponseEntity<Void> response = createUser(TEST_USER_USERNAME, TEST_USER_PASSWORD, createUserRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DirtiesContext
    void ensureAdminCanCreateUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testuser");
        createUserRequest.setPassword("abc123");
        createUserRequest.setRole("user");
        ResponseEntity<Void> response = createUser(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, createUserRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DirtiesContext
    void ensureAdminCanViewNewlyCreateUser() throws Exception {
            CreateUserRequest createUserRequest = new CreateUserRequest();
            createUserRequest.setUsername("testuser");
            createUserRequest.setPassword("abc123");
            createUserRequest.setRole("user");
            ResponseEntity<Void> createResponse = createUser(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, createUserRequest);
            assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            ResponseEntity<String> getResponse = restTemplate.withBasicAuth(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD)
                    .getForEntity(
                            createResponse.getHeaders().getLocation().getPath(),
                            String.class
                    );
            GetUserResponse userDetails = objectMapper.readValue(
                    getResponse.getBody(),
                    GetUserResponse.class
            );

            assertThat(userDetails.getUsername()).isEqualTo("testuser");
            assertThat(userDetails.getRole()).isEqualTo("ROLE_user");
    }

    @Test
    @DirtiesContext
    void ensureNewlyCreatedUserCanViewTheirOwnUser() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testuser");
        createUserRequest.setPassword("abc123");
        createUserRequest.setRole("user");
        ResponseEntity<Void> createResponse = createUser(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, createUserRequest);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> getResponse = restTemplate.withBasicAuth("testuser", "abc123")
                .getForEntity(
                        createResponse.getHeaders().getLocation().getPath(),
                        String.class
                );
        GetUserResponse userDetails = objectMapper.readValue(
                getResponse.getBody(),
                GetUserResponse.class
        );

        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getRole()).isEqualTo("ROLE_user");
    }

    @Test
    void ensureUserCanViewOwnDetails() throws Exception {
        ResponseEntity<String> response = getUser(TEST_USER_USERNAME, TEST_USER_PASSWORD, TEST_USER_USERNAME);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GetUserResponse userResponse = objectMapper.readValue(
                response.getBody(),
                GetUserResponse.class
        );
        assertThat(userResponse.getUsername()).isEqualTo(TEST_USER_USERNAME);
        assertThat(userResponse.getRole()).isEqualTo("ROLE_user");
    }

    @Test
    void ensureAdminCanViewOwnDetails() throws Exception {
        ResponseEntity<String> response = getUser(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, TEST_ADMIN_USERNAME);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GetUserResponse userResponse = objectMapper.readValue(
                response.getBody(),
                GetUserResponse.class
        );
        assertThat(userResponse.getUsername()).isEqualTo(TEST_ADMIN_USERNAME);
        assertThat(userResponse.getRole()).isEqualTo("ROLE_admin");
    }

    @Test
    void ensureAdminCanViewUserDetails() throws Exception {
        ResponseEntity<String> response = getUser(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, TEST_USER_USERNAME);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GetUserResponse userResponse = objectMapper.readValue(
                response.getBody(),
                GetUserResponse.class
        );
        assertThat(userResponse.getUsername()).isEqualTo(TEST_USER_USERNAME);
        assertThat(userResponse.getRole()).isEqualTo("ROLE_user");
    }

    @Test
    void ensureUserCannotViewOtherUserDetails() {
        ResponseEntity<String> response = getUser(TEST_USER_USERNAME, TEST_USER_PASSWORD, TEST_ADMIN_PASSWORD);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<String> getUser(String username, String password, String user) {
        return restTemplate.withBasicAuth(username, password).getForEntity(
                "/user/" + user,
                String.class
        );
    }

    private ResponseEntity<Void> createUser(String username, String password, CreateUserRequest createUserRequest) {
        HttpEntity<CreateUserRequest> request = new HttpEntity<>(createUserRequest);
        return restTemplate.withBasicAuth(username, password)
                .exchange(
                        "/user",
                        HttpMethod.PUT,
                        request,
                        Void.class
                );
    }
}
