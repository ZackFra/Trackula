package com.trackula.track.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trackula.track.TrackApplication;
import com.trackula.track.model.TimerEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.util.List;

import static com.trackula.track.controller.TestDataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void makeData() {
        makeControllerData(jdbcTemplate, passwordEncoder, jdbcUserDetailsManager);
    }

    @Test
    void ensureUserCanInvokeGetTimerEntries() throws Exception {
        ResponseEntity<String> response = restTemplateWithBasicAuthForUser(restTemplate)
                .getForEntity("/timer-entry", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<TimerEntry> timerEntries = objectMapper.readValue(
                response.getBody(),
                new TypeReference<List<TimerEntry>>() {}
        );
        assertThat(timerEntries.size()).isEqualTo(1);
        TimerEntry timerEntry = timerEntries.getFirst();
        assertThat(timerEntry.id()).isEqualTo(USER_TIMER_ENTRY_ID);
    }

    @Test
    void ensureAdminCanInvokeGetTimerEntries() throws Exception {
        ResponseEntity<String> response = restTemplateWithBasicAuthForAdmin(restTemplate)
                .getForEntity("/timer-entry", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TimerEntry> timerEntries = objectMapper.readValue(
                response.getBody(),
                new TypeReference<List<TimerEntry>>() {}
        );
        assertThat(timerEntries.size()).isEqualTo(2);
    }

    @Test
    void ensureUserCannotSeeTimerEntriesTheyDoNotOwn() {
        ResponseEntity<String> response = restTemplateWithBasicAuthForUser(restTemplate)
                .getForEntity("/timer-entry/0", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void ensureUserCanViewTheirTimerEntry() throws Exception {
        ResponseEntity<String> response = restTemplateWithBasicAuthForUser(restTemplate)
                .getForEntity("/timer-entry/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TimerEntry timerEntry;
        timerEntry = objectMapper.readValue(response.getBody(), TimerEntry.class);
        assertThat(timerEntry.id()).isEqualTo(1);

    }

    @Test
    void ensureAdminCanViewTheirTimerEntry() throws Exception {
        ResponseEntity<String> response = restTemplateWithBasicAuthForAdmin(restTemplate)
                .getForEntity("/timer-entry/0", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TimerEntry timerEntry = objectMapper.readValue(response.getBody(), TimerEntry.class);
        assertThat(timerEntry.id()).isEqualTo(0);
    }

    @Test
    @DirtiesContext
    void ensureUserCanCreateTimerEntry() throws Exception {
        TimerEntry timerEntry = new TimerEntry(
                null,
                null,
                3600L
        );
        HttpEntity<TimerEntry> request = new HttpEntity<>(timerEntry);
        ResponseEntity<Void> response = restTemplateWithBasicAuthForUser(restTemplate)
                .postForEntity("/timer-entry", request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = response.getHeaders().getLocation();
        assertThat(location).isNotNull();
        ResponseEntity<String> getResponse = restTemplateWithBasicAuthForUser(restTemplate)
                .getForEntity(location.getPath(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TimerEntry newTimerEntry = objectMapper.readValue(
                getResponse.getBody(),
                TimerEntry.class
        );
        assertThat(newTimerEntry.id()).isNotNull();
    }

    @Test
    @DirtiesContext
    void ensureAdminCanCreateTimerEntry() throws Exception {
        TimerEntry timerEntry = new TimerEntry(
                null,
                null,
                3600L
        );
        HttpEntity<TimerEntry> request = new HttpEntity<>(timerEntry);
        ResponseEntity<Void> response = restTemplateWithBasicAuthForAdmin(restTemplate)
                .postForEntity("/timer-entry", request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = response.getHeaders().getLocation();
        assertThat(location).isNotNull();
        ResponseEntity<String> getResponse = restTemplateWithBasicAuthForAdmin(restTemplate)
                .getForEntity(location.getPath(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TimerEntry newTimerEntry = objectMapper.readValue(
                getResponse.getBody(),
                TimerEntry.class
        );
        assertThat(newTimerEntry.id()).isNotNull();
    }
}
