package com.johnny.petscheduler.utils;

import com.johnny.petscheduler.entities.service.ServiceRate;
import com.johnny.petscheduler.entities.service.ServiceType;
import com.johnny.petscheduler.exceptions.InvalidServiceRateException;

/**
 * Contains utility methods for various operations regarding for a
 * specific ServiceOffering.
 */
public class ServiceOfferingUtil
{
    private ServiceOfferingUtil(){}

    public static void validateServiceOffering(final ServiceType serviceType,
                                               final ServiceRate serviceRate)
    {
        if (serviceType == null || serviceRate == null) {
            throw new IllegalArgumentException(
                    "Service type and rate are required"
            );
        }

        if (!serviceType.isValidRate(serviceRate)) {
            throw new InvalidServiceRateException(
                    "Service rate " + serviceRate + " is not supported for " + serviceType
            );
        }
    }
}
