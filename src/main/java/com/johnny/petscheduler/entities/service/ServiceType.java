package com.johnny.petscheduler.entities.service;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

@Getter
public enum ServiceType
{
    DAY_CARE(
            ServiceRate.PER_HOUR,
            ServiceRate.PER_SESSION
    ),

    DOG_WALKING(ServiceRate.PER_WALK),

    PET_SITTING(
            ServiceRate.PER_HOUR,
            ServiceRate.PER_SESSION
    ),

    PET_TRAINING(
            ServiceRate.PER_HOUR,
            ServiceRate.PER_SESSION
    ),

    DROP_IN_VISIT(ServiceRate.PER_SESSION),

    GROOM(ServiceRate.PER_SESSION);

    private final Set<ServiceRate> allowedRates;

    private ServiceType(ServiceRate... rates) {
        this.allowedRates = Set.copyOf(Arrays.asList(rates));
    }

    public boolean supportsRate(final ServiceRate rate) {
        return allowedRates.contains(rate);
    }
}
