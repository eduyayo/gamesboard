package com.pigdroid.social.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pigdroid.social.dto.ExampleUserDetails;
import com.pigdroid.social.model.user.User;
import com.pigdroid.social.repository.UserRepository;

/**
 * This class loads the requested user by using a Spring Data JPA repository.
 * @author eduyayo@gmail.com
 */
public class RepositoryUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryUserDetailsService.class);

    @Autowired
    private UserRepository repository;

    public RepositoryUserDetailsService() {
    }

    /**
     * Loads the user information.
     * @param username  The username of the requested user.
     * @return  The information of the user.
     * @throws UsernameNotFoundException    Thrown if no user is found with the given username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.debug("Loading user by username: {}", username);

        User user = repository.findByEmail(username);
        LOGGER.debug("Found user: {}", user);

        if (user == null) {
            throw new UsernameNotFoundException("No user found with username: " + username);
        }

        ExampleUserDetails principal = ExampleUserDetails.getBuilder()
                .firstName(user.getFirstName())
                .id(user.getId())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .role(user.getRole())
                .socialSignInProvider(user.getSignInProvider())
                .username(user.getEmail())
                .build();

        LOGGER.debug("Returning user details: {}", principal);

        return principal;
    }
}
