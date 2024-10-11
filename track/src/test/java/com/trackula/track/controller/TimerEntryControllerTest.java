package com.trackula.track.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trackula.track.TrackApplication;
import com.trackula.track.dto.CreateTimerEntryRequest;
import com.trackula.track.dto.UpdateCategoryRequest;
import com.trackula.track.dto.UpdateTimerEntryRequest;
import com.trackula.track.model.TimerEntry;
import com.trackula.track.repository.TimerEntryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.relational.core.sql.Update;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.trackula.track.controller.TestDataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@SpringBootTest(classes= TrackApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TimerEntryControllerTest {

    @Autowired
    TimerEntryRepository timerEntryRepository;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

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
        assertThat(timerEntry.id()).isNotNull();
    }

    @Test
    void ensureAdminCanInvokeGetTimerEntries() throws Exception {
        ResponseEntity<String> response = restTemplateWithBasicAuthForAdmin(restTemplate)
                .getForEntity("/timer-entry", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TimerEntry> timerEntries = objectMapper.readValue(
                response.getBody(),
                new TypeReference<>() {}
        );
        assertThat(timerEntries.size()).isEqualTo(2);
    }

    @Test
    void ensureUserCannotSeeTimerEntriesTheyDoNotOwn() {
        Iterable<TimerEntry> timerEntries = timerEntryRepository.findAllByOwner(TEST_ADMIN_USERNAME);
        TimerEntry adminTimerEntry = timerEntries.iterator().next();

        ResponseEntity<String> response = restTemplate.withBasicAuth(TEST_USER_USERNAME, TEST_USER_PASSWORD)
                .getForEntity("/timer-entry/" + adminTimerEntry.id(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void ensureUserCanViewTheirTimerEntry() throws Exception {
        Iterable<TimerEntry> userTimerEntries = timerEntryRepository.findAllByOwner(TEST_USER_USERNAME);
        TimerEntry userTimerEntry = userTimerEntries.iterator().next();
        ResponseEntity<String> response = restTemplateWithBasicAuthForUser(restTemplate)
                .getForEntity("/timer-entry/" + userTimerEntry.id(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TimerEntry timerEntry;
        timerEntry = objectMapper.readValue(response.getBody(), TimerEntry.class);
        assertThat(timerEntry.owner()).isEqualTo(TEST_USER_USERNAME);

    }

    @Test
    void ensureAdminCanViewTheirTimerEntry() throws Exception {
        Iterable<TimerEntry> adminTimerEntries = timerEntryRepository.findAllByOwner(TEST_ADMIN_USERNAME);
        TimerEntry adminTimerEntry = adminTimerEntries.iterator().next();
        ResponseEntity<String> response = restTemplateWithBasicAuthForAdmin(restTemplate)
                .getForEntity("/timer-entry/" + adminTimerEntry.id(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TimerEntry timerEntry = objectMapper.readValue(response.getBody(), TimerEntry.class);
        assertThat(timerEntry.owner()).isEqualTo(TEST_ADMIN_USERNAME);
    }

    @Test
    @DirtiesContext
    void ensureUserCanCreateTimerEntry() throws Exception {
        CreateTimerEntryRequest createTimerEntryRequest = new CreateTimerEntryRequest();
        createTimerEntryRequest.setTimeTracked(3600L);
        HttpEntity<CreateTimerEntryRequest> request = new HttpEntity<>(createTimerEntryRequest);
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
        CreateTimerEntryRequest createTimerEntryRequest = new CreateTimerEntryRequest();
        createTimerEntryRequest.setTimeTracked(3600L);
        HttpEntity<CreateTimerEntryRequest> request = new HttpEntity<>(createTimerEntryRequest);
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

    @Test
    @DirtiesContext
    void ensureAdminCanUpdateTimerEntry() {
        Iterable<TimerEntry> timerEntries = timerEntryRepository.findAllByOwner(TEST_ADMIN_USERNAME);
        TimerEntry timerEntry = timerEntries.iterator().next();
        UpdateTimerEntryRequest updateTimerEntryRequest = new UpdateTimerEntryRequest();
        updateTimerEntryRequest.setTimeTracked(4800L);
        ResponseEntity<Void> response = updateTimerEntry(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, updateTimerEntryRequest, timerEntry.id());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Optional<TimerEntry> updatedTimerEntryOptional = timerEntryRepository.findById(timerEntry.id());
        if(updatedTimerEntryOptional.isEmpty()) {
            fail("Expected timer entry to exist");
        }
        TimerEntry updatedTimerEntry = updatedTimerEntryOptional.get();
        assertThat(updatedTimerEntry.timeTracked()).isEqualTo(4800L);
    }

    @Test
    @DirtiesContext
    void ensureUserCanUpdateTimeTrackedOnEntry() {
        Iterable<TimerEntry> timerEntries = timerEntryRepository.findAllByOwner(TEST_USER_USERNAME);
        TimerEntry timerEntry = timerEntries.iterator().next();
        UpdateTimerEntryRequest updateTimerEntryRequest = new UpdateTimerEntryRequest();
        updateTimerEntryRequest.setTimeTracked(4800L);
        ResponseEntity<Void> response = updateTimerEntry(TEST_USER_USERNAME, TEST_USER_PASSWORD, updateTimerEntryRequest, timerEntry.id());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Optional<TimerEntry> updatedTimerEntryOptional = timerEntryRepository.findById(timerEntry.id());
        if(updatedTimerEntryOptional.isEmpty()) {
            fail("Expected timer entry to exist");
        }
        TimerEntry updatedTimerEntry = updatedTimerEntryOptional.get();
        assertThat(updatedTimerEntry.timeTracked()).isEqualTo(4800L);
    }

    @Test
    void ensureUserCannotUpdateTimerEntryTheyDoNotOwn() {
        Iterable<TimerEntry> timerEntries = timerEntryRepository.findAllByOwner(TEST_ADMIN_USERNAME);
        TimerEntry timerEntry = timerEntries.iterator().next();
        UpdateTimerEntryRequest updateTimerEntryRequest = new UpdateTimerEntryRequest();
        updateTimerEntryRequest.setTimeTracked(4800L);
        ResponseEntity<Void> response = updateTimerEntry(TEST_USER_USERNAME, TEST_USER_PASSWORD, updateTimerEntryRequest, timerEntry.id());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void ensureAdminUserCanChangeTimerEntryOwner() {
        Iterable<TimerEntry> timerEntries = timerEntryRepository.findAllByOwner(TEST_USER_USERNAME);
        TimerEntry timerEntry = timerEntries.iterator().next();
        UpdateTimerEntryRequest updateTimerEntryRequest = new UpdateTimerEntryRequest();
        updateTimerEntryRequest.setOwner(TEST_ADMIN_USERNAME);
        ResponseEntity<Void> response = updateTimerEntry(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, updateTimerEntryRequest, timerEntry.id());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Optional<TimerEntry> updatedTimerEntryOptional = timerEntryRepository.findById(timerEntry.id());
        if(updatedTimerEntryOptional.isEmpty()) {
            fail("Expected timer entry to exist");
        }
        TimerEntry updatedTimerEntry = updatedTimerEntryOptional.get();
        assertThat(updatedTimerEntry.owner()).isEqualTo(TEST_ADMIN_USERNAME);
    }

    @Test
    @DirtiesContext
    void ensureAdminCanDeleteUserTimerEntry() {
        TimerEntry timerEntry = getFirstTimerEntryFor(TEST_USER_USERNAME);
        ResponseEntity<Void> response = deleteTimerEntry(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, timerEntry.id());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DirtiesContext
    void ensureUserCanDeleteTimerEntry() {
        TimerEntry timerEntry = getFirstTimerEntryFor(TEST_USER_USERNAME);
        ResponseEntity<Void> response = deleteTimerEntry(TEST_USER_USERNAME, TEST_USER_PASSWORD, timerEntry.id());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DirtiesContext
    void ensureUserCannotDeleteAdminTimerEntry() {
        TimerEntry timerEntry = getFirstTimerEntryFor(TEST_ADMIN_USERNAME);
        ResponseEntity<Void> response = deleteTimerEntry(TEST_USER_USERNAME, TEST_USER_PASSWORD, timerEntry.id());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void ensureAdminCanDeleteTheirOwnTimerEntry() {
        TimerEntry timerEntry = getFirstTimerEntryFor(TEST_ADMIN_USERNAME);
        ResponseEntity<Void> response = deleteTimerEntry(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, timerEntry.id());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private TimerEntry getFirstTimerEntryFor(String username) {
        Iterable<TimerEntry> timerEntries = timerEntryRepository.findAllByOwner(username);
        TimerEntry timerEntry = timerEntries.iterator().next();
        return timerEntry;
    }

    private ResponseEntity<Void> deleteTimerEntry(String username, String password, Long id) {
        return restTemplate.withBasicAuth(username, password)
                .exchange(
                        "/timer-entry/" + id,
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );
    }

    private ResponseEntity<Void> updateTimerEntry(String username, String password, UpdateTimerEntryRequest updateTimerEntryRequest, Long id) {
        HttpEntity<UpdateTimerEntryRequest> request = new HttpEntity<>(updateTimerEntryRequest);
        return restTemplate.withBasicAuth(username, password).exchange(
                "/timer-entry/" + id,
                HttpMethod.PUT,
                request,
                Void.class
        );
    }
}
