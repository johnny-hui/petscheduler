package com.johnny.petscheduler.entities.service;

public enum ServiceRate
{
    PER_HOUR,     // flexible rate
    PER_SESSION,  // fixed rate
    PER_WALK;    // fixed rate

    public boolean isFixedRate()
    {
        return this == PER_SESSION || this == PER_WALK;
    }

    public boolean isFlexibleRate()
    {
        return this == PER_HOUR;
    }
}