package com.dmurraysd.spring;

import com.dmurraysd.spring.rest.EventStatusController;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Supplier;

@Configuration
public class LiveEventTrackerApplicationConfig {

    @Bean
    public GroupedOpenApi liveEventTrackerApi() {
        return GroupedOpenApi.builder()
                .group("Live Event Tracker API")
                .pathsToMatch("/**")
                .packagesToScan(EventStatusController.class.getPackageName()).build();
    }

    @Bean
    public Supplier<UUID> uuidSupplier() {
        return UUID::randomUUID;
    }
}
