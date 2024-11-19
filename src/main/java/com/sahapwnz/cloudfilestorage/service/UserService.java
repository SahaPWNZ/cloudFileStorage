package com.sahapwnz.cloudfilestorage.service;

import com.sahapwnz.cloudfilestorage.dto.UserRequestDTO;
import com.sahapwnz.cloudfilestorage.entity.User;
import com.sahapwnz.cloudfilestorage.exception.RegistrationException;
import com.sahapwnz.cloudfilestorage.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = passwordEncoder;
    }

    @Transactional
    public void saveUser(User user) {
        if (userRepository.findByLogin(user.getLogin()).isPresent()) {
            throw new RegistrationException("This login: " + user.getLogin() + " is already in use, use another one");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("User '{}' was successfully saved", user.getLogin());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", username)
        ));

        return new UserDetailsImpl(user);
    }

    public User convertToUser(UserRequestDTO userRequestDTO) {
        return User.builder()
                .login(userRequestDTO.getLogin())
                .password(userRequestDTO.getPassword())
                .build();
    }
}
