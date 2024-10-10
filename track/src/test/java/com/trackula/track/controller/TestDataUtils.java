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

    @Transactional
    public static void makeControllerData(
            TimerEntryJdbcRepository timerEntryJdbcRepository,
            CategoryJdbcRepository categoryJdbcRepository,
            TimerEntryCategoryJdbcRepository timerEntryCategoryJdbcRepository,
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

        TimerEntry adminTimerEntry = timerEntryJdbcRepository.save(
                new TimerEntry(
                        null,
                        "test-admin",
                        3600L
                )
        );
        Category newCategory = categoryJdbcRepository.save(
                new Category(
                        null,
                        TEST_CATEGORY_NAME,
                        "test-admin"
                )
        );
        TimerEntryCategory timerEntryCategoryForAdmin = timerEntryCategoryJdbcRepository.save(
                new TimerEntryCategory(
                        null,
                        adminTimerEntry.id(),
                        newCategory.id(),
                        "test-admin"
                )
        );
        TimerEntry userTimerEntry = timerEntryJdbcRepository.save(
                new TimerEntry(
                        null,
                        "test-user",
                        1800L
                )
        );

        TimerEntryCategory newTimerEntryCategory = timerEntryCategoryJdbcRepository.save(
                new TimerEntryCategory(
                        null,
                        userTimerEntry.id(),
                        newCategory.id(),
                        "test-user"
                )
        );
    }

    public static TestRestTemplate restTemplateWithBasicAuthForAdmin(TestRestTemplate restTemplate) {
        return restTemplate.withBasicAuth(TEST_ADMIN_USERNAME, TEST_ADMIN_PASSWORD);
    }

    public static TestRestTemplate restTemplateWithBasicAuthForUser(TestRestTemplate restTemplate) {
        return restTemplate.withBasicAuth(TEST_USER_USERNAME, TEST_USER_PASSWORD);
    }
}
