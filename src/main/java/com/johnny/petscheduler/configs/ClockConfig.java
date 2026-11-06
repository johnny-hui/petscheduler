package com.johnny.petscheduler.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * A configuration class that loads and manages an application-wide clock.
 */
@Configuration
public class ClockConfig
{
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}