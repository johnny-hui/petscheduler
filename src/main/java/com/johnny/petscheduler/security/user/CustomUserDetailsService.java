package com.johnny.petscheduler.security.user;

import com.johnny.petscheduler.DAO.UserDAO;
import com.johnny.petscheduler.entities.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("[+] AUTHENTICATION ERROR: User not found");
        }
        return user;
    }
}