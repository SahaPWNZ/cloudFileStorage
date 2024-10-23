package com.sahapwnz.cloudfilestorage;

import com.sahapwnz.cloudfilestorage.entity.User;
import com.sahapwnz.cloudfilestorage.repository.UserRepository;
import com.sahapwnz.cloudfilestorage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

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
    void testSaveUserSuccess() {
        User user = User.builder()
                .login("testUser")
                .password("test")
                .build();
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        Assertions.assertTrue(userService.saveUser(user));
        log.info("testSaveUserSuccess() pass");
    }

    @Test
    void testSaveUserIfUserExist() {
        User user = User.builder()
                .login("testUser")
                .password("test")
                .build();
        when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
        Assertions.assertFalse(userService.saveUser(user));
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
