package com.trackula.track.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.trackula.track.TrackApplication;
import com.trackula.track.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.annotation.DirtiesContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @BeforeEach
    void makeData() {
        TestDataUtils.makeControllerData(jdbcTemplate, passwordEncoder, jdbcUserDetailsManager);
    }

    @Test
    void ensureGetRequestReturnsACategoryWhenExists() {
        ResponseEntity<String> response = getCategoryByIdAsAdmin("0");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("test");
    }

    @Test
    void ensureGetRequests404sWhenCategoryDoesNotExist() {
        ResponseEntity<String> response = getCategoryByIdAsAdmin("999");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DirtiesContext
    void ensurePostRequestCreatesNewCategory() {
        Category category = new Category(null, "test2", TestDataUtils.TEST_ADMIN_USERNAME);
        ResponseEntity<Void> putResponse = postCategoryAsAdmin(category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = putResponse.getHeaders().getLocation();
        assertThat(location).isNotNull();

        ResponseEntity<String> getResponse = getCategoryByPathAsAdmin(location.getPath());
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("test2");
    }

    @Test
    void ensurePostRequestWithIdDoesNotCreateNewCategory() {
        Category category = new Category(1L, "test2", TestDataUtils.TEST_ADMIN_USERNAME);
        ResponseEntity<Void> putResponse = postCategoryAsAdmin(category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ResponseEntity<String> fetchedCategoryResponse = getCategoryByIdAsAdmin("1");
        assertThat(fetchedCategoryResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void ensurePostRequestWithExistingNameReturnsConflict() {
        Category category = new Category(null, "test", TestDataUtils.TEST_ADMIN_USERNAME);
        ResponseEntity<Void> putResponse = postCategoryAsAdmin(category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DirtiesContext
    void ensurePutToNameGoesThrough() {
        Category category = new Category(0L, "test update", TestDataUtils.TEST_ADMIN_USERNAME);
        ResponseEntity<Void> putResponse = putCategoryAsAdmin("0", category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void ensurePutThatDoesNotModifyNameReturnsNotModified() {
        Category category = new Category(0L, "test", TestDataUtils.TEST_ADMIN_USERNAME);
        ResponseEntity<Void> putResponse = putCategoryAsAdmin("0", category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_MODIFIED);
    }

    @Test
    void ensurePutToInvalidCategoryReturnsNotFound() {
        Category category = new Category(1L, "new test", TestDataUtils.TEST_ADMIN_USERNAME);
        ResponseEntity<Void> putResponse = putCategoryAsAdmin("1", category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void ensurePutIdMismatchReturnsBadRequest() {
        Category category = new Category(1L, "new test", TestDataUtils.TEST_ADMIN_USERNAME);
        ResponseEntity<Void> putResponse = putCategoryAsAdmin("0", category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DirtiesContext
    void ensurePutWithNullIdSucceeds() {
        Category category = new Category(null, "new test", TestDataUtils.TEST_ADMIN_USERNAME);
        ResponseEntity<Void> putResponse = putCategoryAsAdmin("0", category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DirtiesContext
    void ensureDeleteCategorySucceedsForValidId() {
        ResponseEntity<Void> deleteResponse = deleteCategoryAsAdmin("0");
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<String> getResponse = getCategoryByIdAsAdmin("0");
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void ensureGetAllCategoriesReturnsAllCategories() throws Exception {
        ResponseEntity<String> categoriesResponse = getAllCategoriesAsAdmin();
        List<Category> categories = objectMapper.readValue(
                categoriesResponse.getBody(),
                new TypeReference<>() {}
        );
        assertThat(categories.size()).isEqualTo(1);
        Category category = categories.getFirst();
        assertThat(category.name()).isEqualTo("test");
        assertThat(category.id()).isEqualTo(0L);
    }

    @Test
    void ensureUserRoleCanSeeAllCategories() throws Exception {
        ResponseEntity<String> categoriesResponse = getAllCategoriesAsUser();
        List<Category> categories = objectMapper.readValue(
                categoriesResponse.getBody(),
                new TypeReference<>() {}
        );
        assertThat(categories.size()).isEqualTo(1);
        Category category = categories.getFirst();
        assertThat(category.name()).isEqualTo("test");
        assertThat(category.id()).isEqualTo(0L);
    }

    @Test
    void ensureUserRoleCanSeeSpecificCategory() {
        ResponseEntity<String> response = getCategoryByIdAsUser("0");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("test");
    }

    @Test
    void ensureUserRoleCannotDeleteCategories() {
        ResponseEntity<Void> response = deleteCategoryAsUser("0");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void ensureUserRoleCannotUpdateCategory() {
        Category category = new Category(0L, "update test", TestDataUtils.TEST_USER_USERNAME);
        ResponseEntity<Void> response = putCategoryAsUser("0", category);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<String> getAllCategoriesAsUser() {
        return getAllCategories(TestDataUtils.restTemplateWithBasicAuthForUser(restTemplate));
    }

    private ResponseEntity<String> getAllCategoriesAsAdmin() {
        return getAllCategories(TestDataUtils.restTemplateWithBasicAuthForAdmin(restTemplate));
    }

    private ResponseEntity<String> getAllCategories(TestRestTemplate restTemplate) {
        return restTemplate.getForEntity("/category", String.class);
    }

    private ResponseEntity<Void> deleteCategoryAsAdmin(String id) {
        return deleteCategory(TestDataUtils.restTemplateWithBasicAuthForAdmin(restTemplate), id);
    }

    private ResponseEntity<Void> deleteCategoryAsUser(String id) {
        return deleteCategory(TestDataUtils.restTemplateWithBasicAuthForUser(restTemplate), id);
    }

    private ResponseEntity<Void> deleteCategory(TestRestTemplate restTemplate, String id) {
        return restTemplate.exchange(
                "/category/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    private ResponseEntity<Void> putCategoryAsAdmin(String id, Category category) {
        return putCategory(TestDataUtils.restTemplateWithBasicAuthForAdmin(restTemplate), id, category);
    }

    private ResponseEntity<Void> putCategoryAsUser(String id, Category category) {
        return putCategory(TestDataUtils.restTemplateWithBasicAuthForUser(restTemplate), id, category);
    }

    private ResponseEntity<Void> putCategory(TestRestTemplate restTemplate, String id, Category category) {
        HttpEntity<Category> httpEntity = new HttpEntity<>(category);
        return restTemplate.exchange(
                "/category/" + id,
                HttpMethod.PUT,
                httpEntity,
                Void.class
        );
    }

    private ResponseEntity<String> getCategoryByIdAsUser(String id) {
        return getCategoryById(TestDataUtils.restTemplateWithBasicAuthForUser(restTemplate), id);
    }

    private ResponseEntity<String> getCategoryByIdAsAdmin(String id) {
        return getCategoryById(TestDataUtils.restTemplateWithBasicAuthForAdmin(restTemplate), id);
    }

    private ResponseEntity<String> getCategoryById(TestRestTemplate restTemplate, String id) {
        return restTemplate.getForEntity("/category/" + id, String.class);
    }

    private ResponseEntity<String> getCategoryByPathAsAdmin(String path) {
        return TestDataUtils.restTemplateWithBasicAuthForAdmin(restTemplate)
                .getForEntity(path, String.class);
    }
    
    private ResponseEntity<Void> postCategoryAsAdmin(Category category) {
        HttpEntity<Category> putRequest = new HttpEntity<>(category);
        return TestDataUtils.restTemplateWithBasicAuthForAdmin(restTemplate).exchange(
                "/category",
                HttpMethod.POST,
                putRequest,
                Void.class
        );
    }
}