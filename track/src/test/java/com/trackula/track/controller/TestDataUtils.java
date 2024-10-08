package com.trackula.track.controller;

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

    @Transactional
    public static void makeControllerData(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder,
            JdbcUserDetailsManager jdbcUserDetailsManager
    ) {
        if(!jdbcUserDetailsManager.userExists(TEST_ADMIN_USERNAME)) {
            UserDetails testAdmin = User.builder()
                    .username(TEST_ADMIN_USERNAME)
                    .password(passwordEncoder.encode(TEST_ADMIN_PASSWORD))
                    .roles("admin")
                    .build();
            jdbcUserDetailsManager.createUser(testAdmin);
        }
        if(!jdbcUserDetailsManager.userExists(TEST_USER_USERNAME)) {
            UserDetails testUser = User.builder()
                    .username(TEST_USER_USERNAME)
                    .password(passwordEncoder.encode(TEST_USER_PASSWORD))
                    .roles("user")
                    .build();
            jdbcUserDetailsManager.createUser(testUser);
        }


        jdbcTemplate.execute("DELETE FROM timer_entry_category");
        jdbcTemplate.execute("DELETE FROM category");
        jdbcTemplate.execute("DELETE FROM timer_entry");

        jdbcTemplate.execute("INSERT INTO category(id, name, owner) VALUES(0, 'test', 'test-admin')");
        jdbcTemplate.execute("INSERT INTO timer_entry(id, owner, time_tracked) VALUES(0, 'test-admin', 3600)");
        jdbcTemplate.execute("INSERT INTO timer_entry_category(id, timer_entry_id, category_id, owner) VALUES(0, 0, 0, 'test-admin')");

        jdbcTemplate.execute("INSERT INTO timer_entry(id, owner, time_tracked) VALUES(1, 'test-user', 1800)");
        jdbcTemplate.execute("INSERT INTO timer_entry_category(id, timer_entry_id, category_id, owner) VALUES(1, 1, 0, 'test-user')");
    }

    public static TestRestTemplate restTemplateWithBasicAuthForAdmin(TestRestTemplate restTemplate) {
        return restTemplate.withBasicAuth(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD);
    }

    public static TestRestTemplate restTemplateWithBasicAuthForUser(TestRestTemplate restTemplate) {
        return restTemplate.withBasicAuth(TEST_USER_USERNAME, TEST_USER_PASSWORD);
    }
}
