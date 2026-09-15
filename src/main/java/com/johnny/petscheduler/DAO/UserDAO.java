package com.johnny.petscheduler.DAO;

import com.johnny.petscheduler.entities.users.User;
import java.util.List;

public interface UserDAO
{
    // a) Create
    void createUser(final User user);

    // b) Read
    User getUserByEmail(final String email);
    User getUserById(final Long id);
    List<User> getAllUsers();
    boolean doesUserExist(final String email);
    boolean doesUserExist(final Long id);

    // c) Update
    void updateUser(final User user);

    // d) Delete
    void deleteUser(final User user);
}
