package com.sahapwnz.cloudfilestorage.service;

import com.sahapwnz.cloudfilestorage.entity.User;
import com.sahapwnz.cloudfilestorage.exception.RegistrationException;
import com.sahapwnz.cloudfilestorage.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder bCryptPasswordEncoder;

    @Test
    void testSaveUserIfUserExist() {
        User user = User.builder()
                .login("testUser")
                .password("test")
                .build();
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
        RegistrationException thrown = assertThrows(RegistrationException.class, () -> {
            userService.saveUser(user);
        });
        assertEquals("This login: testUser is already in use, use another one", thrown.getMessage());
        log.info("testSaveUserIfUserExist() pass");
    }

    @Test
    public void testSaveUserEncryptsPassword() {
        User user = User.builder()
                .login("testUser")
                .password("test")
                .build();
        when(userRepository.findByLogin("testUser")).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn("SECRETPass12345");

        userService.saveUser(user);
        verify(bCryptPasswordEncoder).encode("test");
        log.info("testSaveUserEncryptsPassword() pass");
    }


}
