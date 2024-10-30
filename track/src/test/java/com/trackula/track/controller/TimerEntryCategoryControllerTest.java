package com.trackula.track.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trackula.track.TrackApplication;
import com.trackula.track.dto.CreateTimerEntryCategoryRequest;
import com.trackula.track.dto.CreateTimerEntryRequest;
import com.trackula.track.model.Category;
import com.trackula.track.model.TimerEntry;
import com.trackula.track.model.TimerEntryCategory;
import com.trackula.track.repository.CategoryRepository;
import com.trackula.track.repository.TimerEntryCategoryRepository;
import com.trackula.track.repository.TimerEntryJdbcRepository;
import com.trackula.track.repository.TimerEntryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.util.List;
import java.util.Optional;

import static com.trackula.track.controller.TestDataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes= TrackApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
public class TimerEntryCategoryControllerTest {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TimerEntryRepository timerEntryRepository;

    @Autowired
    TimerEntryJdbcRepository timerEntryJdbcRepository;

    @Autowired
    TimerEntryCategoryRepository timerEntryCategoryRepository;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username=TEST_ADMIN_USERNAME)
    void ensureAdminCannotCreateConflictingTimerEntryCategory() {
        Category category = getCategory();
        TimerEntry timerEntry = getTimerEntry(TEST_ADMIN_USERNAME);
        CreateTimerEntryCategoryRequest createTimerEntryCategoryRequest = new CreateTimerEntryCategoryRequest();
        createTimerEntryCategoryRequest.setCategoryId(category.id());
        createTimerEntryCategoryRequest.setTimerEntryId(timerEntry.id());

        ResponseEntity<Void> response = createTimerEntryCategory(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, createTimerEntryCategoryRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void ensureUserCannotCreateConflictingTimerEntryCategory() {
        Category category = getCategory();
        TimerEntry timerEntry = getTimerEntry(TEST_USER_USERNAME);
        CreateTimerEntryCategoryRequest createTimerEntryCategoryRequest = new CreateTimerEntryCategoryRequest();
        createTimerEntryCategoryRequest.setCategoryId(category.id());
        createTimerEntryCategoryRequest.setTimerEntryId(timerEntry.id());

        ResponseEntity<Void> response = createTimerEntryCategory(TEST_USER_USERNAME, TEST_USER_PASSWORD, createTimerEntryCategoryRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DirtiesContext
    void ensureAdminCanCreateTimerEntryCategory() {
        TimerEntry timerEntry = createTimerEntry(TEST_ADMIN_USERNAME, 3600L);
        Category category = getCategory();
        CreateTimerEntryCategoryRequest createTimerEntryCategoryRequest = new CreateTimerEntryCategoryRequest();
        createTimerEntryCategoryRequest.setTimerEntryId(timerEntry.id());
        createTimerEntryCategoryRequest.setCategoryId(category.id());
        ResponseEntity<Void> response = createTimerEntryCategory(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, createTimerEntryCategoryRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DirtiesContext
    void ensureUserCanCreateTimerEntryCategory() {
        TimerEntry timerEntry = createTimerEntry(TEST_USER_USERNAME, 3600L);
        Category category = getCategory();
        CreateTimerEntryCategoryRequest createTimerEntryCategoryRequest = new CreateTimerEntryCategoryRequest();
        createTimerEntryCategoryRequest.setTimerEntryId(timerEntry.id());
        createTimerEntryCategoryRequest.setCategoryId(category.id());
        ResponseEntity<Void> response = createTimerEntryCategory(TEST_USER_USERNAME, TEST_USER_PASSWORD, createTimerEntryCategoryRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void ensureRequestsWithoutACategoryGetsRejected() {
        TimerEntry timerEntry = createTimerEntry(TEST_USER_USERNAME, 3600L);
        CreateTimerEntryCategoryRequest createTimerEntryCategoryRequest = new CreateTimerEntryCategoryRequest();
        createTimerEntryCategoryRequest.setTimerEntryId(timerEntry.id());
        createTimerEntryCategoryRequest.setCategoryId(null);
        ResponseEntity<Void> response = createTimerEntryCategory(TEST_USER_USERNAME, TEST_USER_PASSWORD, createTimerEntryCategoryRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void ensureRequestsWithoutATimerEntryGetsRejected() {
        Category category = getCategory();
        CreateTimerEntryCategoryRequest createTimerEntryCategoryRequest = new CreateTimerEntryCategoryRequest();
        createTimerEntryCategoryRequest.setTimerEntryId(null);
        createTimerEntryCategoryRequest.setCategoryId(category.id());
        ResponseEntity<Void> response = createTimerEntryCategory(TEST_USER_USERNAME, TEST_USER_PASSWORD, createTimerEntryCategoryRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void ensureAdminCanGetAllTimerEntryCategoriesForAdminTimerEntry() throws Exception {
        TimerEntry timerEntry = getTimerEntry(TEST_ADMIN_USERNAME);
        assertThat(timerEntry.id()).isNotNull();
        ResponseEntity<String> response = getTimerEntryCategoriesByTimerEntryId(
                TEST_ADMIN_USERNAME,
                TEST_ADMIN_PASSWORD,
                timerEntry.id()
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TimerEntryCategory> timerEntryCategories = objectMapper.readValue(
                response.getBody(),
                new TypeReference<>() {}
        );

        assertThat(timerEntryCategories.size()).isEqualTo(1);
        assertThat(timerEntryCategories.get(0).timerEntryId()).isEqualTo(timerEntry.id());
    }

    @Test
    void ensureAdminCanGetAllTimerEntryCategoriesForUserTimerEntry() throws Exception {
        TimerEntry timerEntry = getTimerEntry(TEST_USER_USERNAME);
        assertThat(timerEntry.id()).isNotNull();
        ResponseEntity<String> response = getTimerEntryCategoriesByTimerEntryId(
                TEST_ADMIN_USERNAME,
                TEST_ADMIN_PASSWORD,
                timerEntry.id()
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TimerEntryCategory> timerEntryCategories = objectMapper.readValue(
                response.getBody(),
                new TypeReference<>() {}
        );

        assertThat(timerEntryCategories.size()).isEqualTo(1);
        assertThat(timerEntryCategories.get(0).timerEntryId()).isEqualTo(timerEntry.id());
    }

    @Test
    void ensureUserCanGetAllTimerEntryCategoriesForUserTimerEntry() throws Exception {
        TimerEntry timerEntry = getTimerEntry(TEST_USER_USERNAME);
        assertThat(timerEntry.id()).isNotNull();
        ResponseEntity<String> response = getTimerEntryCategoriesByTimerEntryId(
                TEST_USER_USERNAME,
                TEST_USER_PASSWORD,
                timerEntry.id()
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TimerEntryCategory> timerEntryCategories = objectMapper.readValue(
                response.getBody(),
                new TypeReference<>() {}
        );

        assertThat(timerEntryCategories.size()).isEqualTo(1);
        assertThat(timerEntryCategories.get(0).timerEntryId()).isEqualTo(timerEntry.id());
    }

    @Test
    void ensureUserCannotGetAllTimerEntryCategoriesForAdminTimerEntry() throws Exception {
        TimerEntry timerEntry = getTimerEntry(TEST_ADMIN_USERNAME);
        assertThat(timerEntry.id()).isNotNull();
        ResponseEntity<String> response = getTimerEntryCategoriesByTimerEntryId(
                TEST_USER_USERNAME,
                TEST_USER_PASSWORD,
                timerEntry.id()
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void ensureAdminCanDeleteAdminTimerEntryCategory() {
        TimerEntryCategory timerEntryCategory = getFirstTimerEntryCategory(TEST_ADMIN_USERNAME);
        ResponseEntity<Void> deleteResponse = deleteTimerEntryCategory(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, timerEntryCategory.id());
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Optional<TimerEntryCategory> timerEntryCategoryOptional = timerEntryCategoryRepository.findById(timerEntryCategory.id());
        assertThat(timerEntryCategoryOptional.isPresent()).isFalse();
    }

    @Test
    @DirtiesContext
    void ensureAdminCanDeleteUserTimerEntryCategory() {
        TimerEntryCategory timerEntryCategory = getFirstTimerEntryCategory(TEST_USER_USERNAME);
        ResponseEntity<Void> deleteResponse = deleteTimerEntryCategory(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, timerEntryCategory.id());
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Optional<TimerEntryCategory> timerEntryCategoryOptional = timerEntryCategoryRepository.findById(timerEntryCategory.id());
        assertThat(timerEntryCategoryOptional.isPresent()).isFalse();
    }

    @Test
    @DirtiesContext
    void ensureUserCanDeleteUserTimerEntryCategory() {
        TimerEntryCategory timerEntryCategory = getFirstTimerEntryCategory(TEST_USER_USERNAME);
        ResponseEntity<Void> deleteResponse = deleteTimerEntryCategory(TEST_USER_USERNAME, TEST_USER_PASSWORD, timerEntryCategory.id());
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Optional<TimerEntryCategory> timerEntryCategoryOptional = timerEntryCategoryRepository.findById(timerEntryCategory.id());
        assertThat(timerEntryCategoryOptional.isPresent()).isFalse();
    }

    @Test
    void ensureUserCannotDeleteAdminTimerEntryCategory() {
        TimerEntryCategory timerEntryCategory = getFirstTimerEntryCategory(TEST_ADMIN_USERNAME);
        ResponseEntity<Void> deleteResponse = deleteTimerEntryCategory(TEST_USER_USERNAME, TEST_USER_PASSWORD, timerEntryCategory.id());
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<Void> deleteTimerEntryCategory(String username, String password, Long id) {
        return restTemplate
                .withBasicAuth(username, password)
                .exchange(
                        "/timer-entry-category/" + id,
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );
    }

    private TimerEntryCategory getFirstTimerEntryCategory(String username) {
        Iterable<TimerEntryCategory> timerEntryCategories = timerEntryCategoryRepository.findAllByOwner(username);
        return timerEntryCategories.iterator().next();
    }

    private ResponseEntity<String> getTimerEntryCategoriesByTimerEntryId(String username, String password, Long timerEntryId) {
        return restTemplate.withBasicAuth(username, password)
                .getForEntity(
                        "/timer-entry-category/" + timerEntryId,
                        String.class
                );
    }

    private TimerEntry createTimerEntry(String owner, Long timeTracked) {
        TimerEntry timerEntry = new TimerEntry(
                null,
                owner,
                timeTracked
        );
        TimerEntry savedTimerEntry = timerEntryJdbcRepository.save(timerEntry);
        return savedTimerEntry;
    }

    private ResponseEntity<Void> createTimerEntryCategory(String username, String password, CreateTimerEntryCategoryRequest createTimerEntryCategoryRequest) {
        HttpEntity<CreateTimerEntryCategoryRequest> request = new HttpEntity<>(createTimerEntryCategoryRequest);
        return restTemplate.withBasicAuth(username, password)
                .postForEntity(
                        "/timer-entry-category",
                        request,
                        Void.class
                );
    }

    private Category getCategory() {
        return categoryRepository.findAll().iterator().next();
    }

    private TimerEntry getTimerEntry(String username) {
        return timerEntryRepository.findAllByOwner(username).iterator().next();
    }
}
