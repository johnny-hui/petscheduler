package com.johnny.petscheduler.security.utils;

import com.johnny.petscheduler.entities.users.User;
import com.johnny.petscheduler.exceptions.MissingRefreshTokenException;
import com.johnny.petscheduler.security.jwt.RefreshToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import static com.johnny.petscheduler.security.configs.SecurityConstants.COOKIE_NAME_JWT_REFRESH;
import static com.johnny.petscheduler.security.configs.SecurityConstants.MISSING_REFRESH_TOKEN_COOKIE_MSG;

/**
 * A utility class that contains methods that assist in
 * authentication endpoints.
 */
public final class AuthUtil
{
    private AuthUtil(){}

    /**
     * Extracts the JWT refresh token from an HttpOnly cookie inside
     * an HTTP request.
     *
     * @param request
     *      An HTTP request.
     *
     * @return <code>refreshToken</code>
     *      A String containing the refresh token
     */
    public static String extractRefreshToken(final HttpServletRequest request) throws MissingRefreshTokenException
    {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME_JWT_REFRESH)) {
                    return cookie.getValue();
                }
            }
        }
        throw new MissingRefreshTokenException(MISSING_REFRESH_TOKEN_COOKIE_MSG);
    }

    /**
     * Creates a RefreshToken entity for persistence in the DB.
     *
     * @param user
     *      A User object of any subtype(PetOwner, PetSitter, PetBusiness)
     *
     * @param hash
     *      A String containing the hash of the JWT refresh token
     *
     * @return <code>RefreshToken</code>
     *      A RefreshToken object
     */
    public static RefreshToken createRefreshTokenEntityForDB(final User user,
                                                             final String hash)
    {
        RefreshToken DBRefreshToken = new RefreshToken();
        DBRefreshToken.setUser(user);
        DBRefreshToken.setTokenHash(hash);
        return DBRefreshToken;
    }
}
