package com.johnny.petscheduler.security.jwt;

/**
 * This record contains data for both JWT access and refresh tokens.
 */
public record TokenPair(
        String access,
        String refresh
){}
