package com.johnny.petscheduler.entities.booking;

public enum BookingStatus
{
    REQUESTED,      // A request to be accepted
    CONFIRMED,	    // Sitter accepted
    DECLINED,	    // Sitter rejected the request
    CANCELLED,	    // A previously accepted booking was canceled
    FULFILLED,      // Service was completed
}
