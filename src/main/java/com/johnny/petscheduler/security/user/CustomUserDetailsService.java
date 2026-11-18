package com.johnny.petscheduler.security.user;

import com.johnny.petscheduler.DAO.UserDAO;
import com.johnny.petscheduler.exceptions.UserNotFoundException;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.ERROR_MESSAGE_GENERAL_USER_NOT_FOUND;

/**
 * This is a custom UserDetailsService wrapper class that tells Spring
 * Security how to load a User from the database and wrap it into a type
 * UserDetail during authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserDAO userDAO;

    @Autowired
    public CustomUserDetailsService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override @NullMarked
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException
    {
        return userDAO
                .getUserByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(ERROR_MESSAGE_GENERAL_USER_NOT_FOUND)
                );
    }
}