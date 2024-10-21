package com.trackula.track.controller;

import com.trackula.track.model.Category;
import com.trackula.track.model.TimerEntry;
import com.trackula.track.model.TimerEntryCategory;
import com.trackula.track.repository.CategoryJdbcRepository;
import com.trackula.track.repository.TimerEntryCategoryJdbcRepository;
import com.trackula.track.repository.TimerEntryJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class TestDataUtils {
    final static String TEST_ADMIN_USERNAME = "test-admin";
    final static String TEST_ADMIN_PASSWORD = "admin-password";
    final static String TEST_USER_USERNAME = "test-user";
    final static String TEST_USER_PASSWORD = "user-password";
    final static String TEST_CATEGORY_NAME = "test";

    public static TestRestTemplate restTemplateWithBasicAuthForAdmin(TestRestTemplate restTemplate) {
        return restTemplate.withBasicAuth(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD);
    }

    public static TestRestTemplate restTemplateWithBasicAuthForUser(TestRestTemplate restTemplate) {
        return restTemplate.withBasicAuth(TEST_USER_USERNAME, TEST_USER_PASSWORD);
    }
}
