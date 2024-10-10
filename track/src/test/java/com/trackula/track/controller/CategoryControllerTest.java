package com.trackula.track.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.trackula.track.TrackApplication;
import com.trackula.track.dto.CreateCategoryRequest;
import com.trackula.track.dto.UpdateCategoryRequest;
import com.trackula.track.model.Category;
import com.trackula.track.model.User;
import com.trackula.track.repository.CategoryJdbcRepository;
import com.trackula.track.repository.CategoryRepository;
import com.trackula.track.repository.TimerEntryCategoryJdbcRepository;
import com.trackula.track.repository.TimerEntryJdbcRepository;
import org.apache.coyote.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.relational.core.sql.Update;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.annotation.DirtiesContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.trackula.track.controller.TestDataUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@SpringBootTest(classes= TrackApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JdbcUserDetailsManager jdbcUserDetailsManager;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    TimerEntryJdbcRepository timerEntryJdbcRepository;

    @Autowired
    CategoryJdbcRepository categoryJdbcRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TimerEntryCategoryJdbcRepository timerEntryCategoryJdbcRepository;

    @BeforeEach
    void makeData() {
        TestDataUtils.makeControllerData(
                timerEntryJdbcRepository,
                categoryJdbcRepository,
                timerEntryCategoryJdbcRepository,
                jdbcTemplate,
                passwordEncoder,
                jdbcUserDetailsManager
        );
    }

    @Test
    void ensureGetRequestReturnsACategoryWhenExists() {
        Category category = getTestCategory();
        ResponseEntity<String> response = getCategoryById(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, category.id());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("test");
    }

    @Test
    void ensureGetRequests404sWhenCategoryDoesNotExist() {
        ResponseEntity<String> response = getCategoryById(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, 999L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DirtiesContext
    void ensurePostRequestCreatesNewCategory() throws Exception {
        CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest();
        createCategoryRequest.setName("test2");
        ResponseEntity<Void> postResponse = postCategory(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, createCategoryRequest);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = postResponse.getHeaders().getLocation();
        assertThat(location).isNotNull();
        Optional<Category> categoryOptional = categoryRepository.findByName("test2");
        assertThat(categoryOptional.isPresent()).isTrue();

        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD)
                .getForEntity(location.getPath(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Category retrievedCategory = objectMapper.readValue(
                getResponse.getBody(),
                Category.class
        );
        assertThat(retrievedCategory.id()).isNotNull();
    }

    @Test
    void ensurePostRequestWithExistingNameReturnsConflict() {
        CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest();
        createCategoryRequest.setName(TEST_CATEGORY_NAME);
        ResponseEntity<Void> putResponse = postCategory(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, createCategoryRequest);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DirtiesContext
    void ensurePutToNameGoesThrough() {
        Category category = getTestCategory();
        UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
        updateCategoryRequest.setName("test2");
        ResponseEntity<Void> putResponse = putCategory(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, category.id(), updateCategoryRequest);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Category updatedCategory = getTestCategory();
        assertThat(updatedCategory.name()).isEqualTo("test2");
    }

    @Test
    void ensurePutToInvalidCategoryReturnsNotFound() {
        UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
        updateCategoryRequest.setName("test");
        ResponseEntity<Void> putResponse = putCategory(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, 12345L, updateCategoryRequest);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void ensureDeleteCategorySucceedsForValidId() {
        Category category = getTestCategory();
        ResponseEntity<Void> deleteResponse = deleteCategory(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, category.id());
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<String> getResponse = getCategoryById(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD, category.id());
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void ensureGetAllCategoriesReturnsAllCategories() throws Exception {
        ResponseEntity<String> categoriesResponse = getAllCategories(TEST_USER_USERNAME, TEST_USER_PASSWORD);
        List<Category> categories = objectMapper.readValue(
                categoriesResponse.getBody(),
                new TypeReference<>() {}
        );
        assertThat(categories.size()).isEqualTo(1);
        Category category = categories.getFirst();
        assertThat(category.name()).isEqualTo(TestDataUtils.TEST_CATEGORY_NAME);
        assertThat(category.id()).isNotNull();
    }

    @Test
    void ensureUserRoleCanSeeAllCategories() throws Exception {
        ResponseEntity<String> categoriesResponse = getAllCategories(TEST_USER_USERNAME, TEST_USER_PASSWORD);
        List<Category> categories = objectMapper.readValue(
                categoriesResponse.getBody(),
                new TypeReference<>() {}
        );
        assertThat(categories.size()).isEqualTo(1);
        Category category = categories.getFirst();
        assertThat(category.name()).isEqualTo(TestDataUtils.TEST_CATEGORY_NAME);
        assertThat(category.id()).isNotNull();
    }

    @Test
    void ensureUserRoleCanSeeSpecificCategory() {
        Category category = getTestCategory();
        ResponseEntity<String> response = getCategoryById(TEST_USER_USERNAME, TEST_USER_PASSWORD,category.id());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("test");
    }

    @Test
    void ensureUserRoleCannotDeleteCategories() {
        Category category = getTestCategory();
        ResponseEntity<Void> response = deleteCategory(TEST_USER_USERNAME, TEST_USER_PASSWORD, category.id());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void ensureUserRoleCannotUpdateCategory() {
        Category category = getTestCategory();
        UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
        updateCategoryRequest.setName("update test");
        ResponseEntity<Void> response = putCategory(TEST_USER_USERNAME, TEST_USER_PASSWORD, category.id(), updateCategoryRequest);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private Category getTestCategory() {
        Optional<Category> categoryOptional = categoryRepository.findByName(TestDataUtils.TEST_CATEGORY_NAME);
        if(categoryOptional.isEmpty()) {
            fail("Expected category optional to not be empty");
        }
        Category category = categoryOptional.get();
        return category;
    }

    private ResponseEntity<String> getAllCategories(String username, String password) {
        return restTemplate
                .withBasicAuth(username, password)
                .getForEntity("/category", String.class);
    }

    private ResponseEntity<Void> deleteCategory(String username, String password, Long id) {
        return restTemplate
                .withBasicAuth(username, password)
                .exchange(
                        "/category/" + id,
                        HttpMethod.PUT,
                        null,
                        Void.class
                );
    }

    private ResponseEntity<Void> putCategory(String username, String password, Long id, UpdateCategoryRequest updateCategoryRequest) {
        HttpEntity<UpdateCategoryRequest> request = new HttpEntity<>(updateCategoryRequest);
        return restTemplate.withBasicAuth(username, password).exchange(
                "/category/" + id,
                HttpMethod.PUT,
                request,
                Void.class
        );
    }

    private ResponseEntity<String> getCategoryById(String username, String password, Long id) {
        return restTemplate.withBasicAuth(username, password)
                .getForEntity("/category/" + String.valueOf(id), String.class);
    }

    private ResponseEntity<Void> postCategory(String username, String password, CreateCategoryRequest createCategoryRequest) {
        HttpEntity<CreateCategoryRequest> request = new HttpEntity<>(createCategoryRequest);
        return restTemplate
                .withBasicAuth(username, password).postForEntity(
                        "/category",
                        request,
                        Void.class
                );
    }
}