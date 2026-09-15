package com.johnny.petscheduler.DAO;

import com.johnny.petscheduler.entities.users.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {
    private final EntityManager em;

    public UserDAOImpl(EntityManager em) {
        this.em = em;
    }

    /**
     * Persists a new User to the database.
     *
     * @param user
     *      Any child of a User object (Owner, Sitter, Business)
     */
    @Override
    public void createUser(final User user) {
        em.persist(user);
    }

    /**
     * Finds a User in the database from a provided email.
     *
     * @param email
     *      A String representing the email address of the user
     *
     * @return User
     *      Any sub-class of the User object
     */
    @Override
    public User getUserByEmail(final String email) {
        try {
            return em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Returns a User by an id.
     *
     * @param id
     *      A Long for the id of the user
     *
     * @return User
     *      Any sub-class of the User object
     */
    @Override
    public User getUserById(final Long id) {
        try {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.id = :id", User.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    /**
     * Checks if a specific User exists provided the email.
     *
     * @param email
     *      A String containing the email address
     *
     * @return bool (T/F)
     *      True if exists; false otherwise
     */
    @Override
    public boolean doesUserExist(final String email) {
        Long count = em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    /**
     * Checks whether a User exists provided the email.
     *
     * @param id
     *      A Long for the id of the User
     *
     * @return bool (T/F)
     *      True if exists; false otherwise
     */
    @Override
    public boolean doesUserExist(final Long id) {
        Long count = em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count <= 0;
    }

    /**
     * Returns all Users from the database.
     *
     * @return List<User>
     *     A list of User objects
     */
    @Override
    public List<User> getAllUsers() {
        return em
                .createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }

    /**
     * Updates a specific User from the database.
     *
     * @param user
     *      The User object to be updated
     */
    @Override
    public void updateUser(final User user) {
        em.merge(user);
    }

    /**
     * Deletes a specific User from the database.
     *
     * @param user
     *      The User to be deleted
     */
    @Override
    public void deleteUser(final User user) {
        em.remove(user);
    }
}
