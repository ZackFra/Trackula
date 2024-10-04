package com.trackula.controller;

import com.trackula.track.TrackApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(controllers= CategoryController.class)
@SpringBootTest(classes= TrackApplication.class)
public class CategoryControllerTest {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void ensureGetRequestWorks() {
        ResponseEntity<String> response = restTemplate.getForEntity("/category/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
