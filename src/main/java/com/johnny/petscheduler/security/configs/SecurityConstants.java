package com.johnny.petscheduler.security.configs;

public class SecurityConstants
{
    // Prevent Instantiation
    private SecurityConstants(){}

    // JWT Constants
    public static final String TOKEN_PREFIX_BEARER = "Bearer ";
    public static final String AUTH_HEADER_STRING = "Authorization";
    public static final String TOKEN_TYPE_HEADER = "token_type";
    public static final String JWT_ACCESS = "access";
    public static final String JWT_REFRESH = "refresh";
    public static final String UNAUTHORIZED_ERROR_TITLE = "Authentication Failed";
    public static final String UNAUTHORIZED_ERROR_MSG = "401 Unauthorized - Authentication is required to access this resource";
    public static final String MISSING_REFRESH_TOKEN_COOKIE_MSG = "No refresh token cookie was found!";
    public static final String MISSING_JWT_REFRESH_ERROR_TITLE = "Refresh Token was not provided";
    public static final String JWT_REFRESH_NOT_FOUND_TITLE = "Refresh Token Not Found";
    public static final String JWT_REFRESH_NOT_FOUND_MSG = "No matching refresh token was found!";

    // HttpOnly Cookie constants
    public static final String COOKIE_NAME_JWT_REFRESH = "refreshToken";
    public static final boolean COOKIE_TOGGLE_HTTPS = false;
    public static final String COOKIE_PATH = "/api/auth";
}
