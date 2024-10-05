package com.trackula.track.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.trackula.track.TrackApplication;
import com.trackula.track.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import javax.swing.text.Document;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes= TrackApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void ensureGetRequestReturnsACategoryWhenExists() {
        ResponseEntity<String> response = getCategoryById("0");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("test");
    }

    @Test
    void ensureGetRequests404sWhenCategoryDoesNotExist() {
        ResponseEntity<String> response = getCategoryById("999");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DirtiesContext
    void ensurePostRequestCreatesNewCategory() {
        Category category = new Category(null, "test2");
        ResponseEntity<Void> putResponse = postCategory(category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = putResponse.getHeaders().getLocation();
        assertThat(location).isNotNull();

        ResponseEntity<String> getResponse = getCategoryByPath(location.getPath());
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();
        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("test2");
    }

    @Test
    void ensurePostRequestWithIdDoesNotCreateNewCategory() {
        Category category = new Category(1L, "test2");
        ResponseEntity<Void> putResponse = postCategory(category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ResponseEntity<String> fetchedCategoryResponse = getCategoryById("1");
        assertThat(fetchedCategoryResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void ensurePostRequestWithExistingNameReturnsConflict() {
        Category category = new Category(null, "test");
        ResponseEntity<Void> putResponse = postCategory(category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DirtiesContext
    void ensurePutToNameGoesThrough() {
        Category category = new Category(0L, "test update");
        ResponseEntity<Void> putResponse = putCategory("0", category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void ensurePutThatDoesNotModifyNameReturnsNotModified() {
        Category category = new Category(0L, "test");
        ResponseEntity<Void> putResponse = putCategory("0", category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_MODIFIED);
    }

    @Test
    void ensurePutToInvalidCategoryReturnsNotFound() {
        Category category = new Category(1L, "new test");
        ResponseEntity<Void> putResponse = putCategory("1", category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void ensurePutIdMismatchReturnsBadRequest() {
        Category category = new Category(1L, "new test");
        ResponseEntity<Void> putResponse = putCategory("0", category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DirtiesContext
    void ensurePutWithNullIdSucceeds() {
        Category category = new Category(null, "new test");
        ResponseEntity<Void> putResponse = putCategory("0", category);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DirtiesContext
    void ensureDeleteCategorySucceedsForValidId() {
        ResponseEntity<Void> deleteResponse = deleteCategory("0");
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<String> getResponse = getCategoryById("0");
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void ensureGetAllCategoriesReturnsAllCategories() throws Exception {
        ResponseEntity<String> categoriesResponse = getAllCategories();
        DocumentContext documentContext = JsonPath.parse(categoriesResponse);
        List<Category> categories = objectMapper.readValue(
                categoriesResponse.getBody(),
                new TypeReference<>() {}
        );
        assertThat(categories.size()).isEqualTo(1);
        Category category = categories.getFirst();
        assertThat(category.name()).isEqualTo("test");
        assertThat(category.id()).isEqualTo(0L);
    }

    private ResponseEntity<String> getAllCategories() {
        return restTemplate.getForEntity("/category", String.class);
    }

    private ResponseEntity<Void> deleteCategory(String id) {
        return restTemplate
                .exchange(
                        "/category/" + id,
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );
    }

    private ResponseEntity<Void> putCategory(String id, Category category) {
        HttpEntity<Category> httpEntity = new HttpEntity<>(category);
        return restTemplate.exchange(
                "/category/" + id,
                HttpMethod.PUT,
                httpEntity,
                Void.class
        );
    }

    private ResponseEntity<String> getCategoryById(String id) {
        return restTemplate.getForEntity("/category/" + id, String.class);
    }

    private ResponseEntity<String> getCategoryByPath(String path) {
        return restTemplate.getForEntity(path, String.class);
    }
    
    private ResponseEntity<Void> postCategory(Category category) {
        HttpEntity<Category> putRequest = new HttpEntity<>(category);
        return restTemplate.exchange(
                "/category",
                HttpMethod.POST,
                putRequest,
                Void.class
        );
    }
}