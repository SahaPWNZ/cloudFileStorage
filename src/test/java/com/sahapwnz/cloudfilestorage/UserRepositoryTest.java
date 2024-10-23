package com.sahapwnz.cloudfilestorage;

import com.sahapwnz.cloudfilestorage.entity.User;
import com.sahapwnz.cloudfilestorage.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class UserRepositoryTest {

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
}


