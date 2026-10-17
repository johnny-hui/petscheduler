package com.johnny.petscheduler.security.jwt.DAO;

import com.johnny.petscheduler.security.jwt.RefreshToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RefreshTokenDAOImpl implements RefreshTokenDAO
{
    private final EntityManager em;

    @Autowired
    public RefreshTokenDAOImpl(EntityManager entityManager)
    {
        this.em = entityManager;
    }

    @Override
    public void create(final RefreshToken refreshToken) {
        em.persist(refreshToken);
    }

    @Override
    public Optional<RefreshToken> getRefreshTokenByID(final Long id)
    {
        TypedQuery<RefreshToken> query = em.createQuery(
                "SELECT rt FROM RefreshToken rt WHERE rt.id = :id",
                RefreshToken.class
        );
        query.setParameter("id", id);
        return query.getResultStream().findFirst();
    }

    @Override
    public Optional<RefreshToken> getRefreshTokenByUserID(final Long userId)
    {
        TypedQuery<RefreshToken> query = em.createQuery(
                "SELECT rt FROM RefreshToken rt WHERE rt.user.id = :userId",
                RefreshToken.class
        );
        query.setParameter("userId", userId);
        return query.getResultStream().findFirst();
    }

    @Override
    public void delete(final RefreshToken refreshToken) {
        em.remove(refreshToken);
    }

    @Override
    public void deleteByUserID(final Long userId) {
        em.createQuery("""
            DELETE FROM RefreshToken rt
            WHERE rt.user.id = :userId
            """)
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
