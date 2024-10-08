package com.trackula.track.controller;

import com.trackula.track.TrackApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import static com.trackula.track.controller.TestDataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes= TrackApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TimerEntryControllerTest {

    @Autowired
    JdbcUserDetailsManager jdbcUserDetailsManager;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void makeData() {
        makeControllerData(jdbcTemplate, passwordEncoder, jdbcUserDetailsManager);
    }

    @Test
    void ensureUserCanInvokeGetTimerEntries() {
        ResponseEntity<String> response = restTemplateWithBasicAuthForUser(restTemplate)
                .getForEntity("/timer-entry", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void ensureUserCanViewTheirTimerEntry() {
        ResponseEntity<String> response = restTemplateWithBasicAuthForUser(restTemplate)
                .getForEntity("/timer-entry/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void ensureAdminCanViewTheirTimerEntry() {
        ResponseEntity<String> response = restTemplateWithBasicAuthForAdmin(restTemplate)
                .getForEntity("/timer-entry/0", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
