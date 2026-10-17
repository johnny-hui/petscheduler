package com.johnny.petscheduler.security.jwt.DAO;

import com.johnny.petscheduler.security.jwt.RefreshToken;

import java.util.Optional;

public interface RefreshTokenDAO
{
    // Create
    void create(final RefreshToken refreshToken);

    // Read
    Optional<RefreshToken> getRefreshTokenByID(final Long id);
    Optional<RefreshToken> getRefreshTokenByUserID(final Long userId);

    // Delete
    void delete(final RefreshToken refreshToken);
}
