package com.sahapwnz.cloudfilestorage.repository;

import com.sahapwnz.cloudfilestorage.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
//@Import(UserRepositoryTest.TestConfig.class)
@Testcontainers
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest

class UserRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer
            = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.driver-class-name",()->"org.postgresql.Driver");
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
//        registry.add("spring.jpa.hibernate.ddl-auto",()->"update");
        registry.add("spring.jpa.show-sql",()->"true");
    }

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void userRepoConnectionTest() {
        User user = User.builder()
                .login("testUser")
                .password("test")
                .build();
        userRepository.save(user);
        User foundUser = userRepository.findByLogin("testUser").orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getLogin()).isEqualTo("testUser");
        log.info("userRepoConnectionTest() pass");
    }

    @Test
    void testFindNonExistentUser() {
        User foundUser = userRepository.findByLogin("nonExistentUser").orElse(null);
        assertThat(foundUser).isNull();
        log.info("testFindNonExistentUser() pass");
    }

    @Test
    void userLoginConstraintExceptionTest() {
        User user = User.builder()
                .login("testUser")
                .password("test")
                .build();
        User user1 = User.builder()
                .login("testUser")
                .password("test")
                .build();
        userRepository.save(user);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user1));
        log.info("userLoginConstraintExceptionTest() pass");
    }

//    @TestConfiguration
//    static class TestConfig {
//        // Здесь можно определить дополнительные бины, если необходимо
//    }
}


