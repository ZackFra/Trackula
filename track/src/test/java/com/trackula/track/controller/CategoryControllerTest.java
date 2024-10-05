package com.trackula.track.controller;

import com.trackula.track.TrackApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes= TrackApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void ensureGetRequestWorks() {
        ResponseEntity<String> response = restTemplate.getForEntity("/category/0", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}