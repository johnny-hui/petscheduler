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
}
