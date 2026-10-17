package com.johnny.petscheduler.security.utils;

import org.springframework.http.ResponseCookie;
import java.time.Duration;

import static com.johnny.petscheduler.security.configs.SecurityConstants.*;

/**
 * A utility class that provides functions for the creation of various HTTP cookies.
 */
public final class CookieUtil
{
    private CookieUtil(){}

    /**
     * Creates an HttpOnly refresh-token cookie.
     *
     * @param refreshToken
     *      A String for the refresh token
     *
     * @param maxAge
     *      A Duration object for the max age of the cookie
     *
     * @return <code>ResponseCookie</code>
     *      A ResponseCookie object with refresh token attached
     */
    public static ResponseCookie createRefreshTokenCookie(final String refreshToken,
                                                          final Duration maxAge)
    {
        return ResponseCookie
                .from(COOKIE_NAME_JWT_REFRESH, refreshToken)
                .httpOnly(true)
                .secure(COOKIE_TOGGLE_HTTPS) // true == requires HTTPS in production
                .sameSite("Lax")
                .path(COOKIE_PATH) // => defines path(s) where the cookie is required
                .maxAge(maxAge)
                .build();
    }


    /**
     * Creates a cookie that clears the existing cookie containing the refresh token
     * stored in the client's browser.
     *
     * @return <code>ResponseCookie</code>
     *      A ResponseCookie object
     */
    public static ResponseCookie clearRefreshTokenCookie()
    {
        return ResponseCookie
                .from(COOKIE_NAME_JWT_REFRESH, "")
                .httpOnly(true)
                .secure(COOKIE_TOGGLE_HTTPS)
                .sameSite("Lax")
                .path(COOKIE_PATH)
                .maxAge(Duration.ZERO)
                .build();
    }
}
