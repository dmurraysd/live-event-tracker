package com.dmurraysd.spring.scheduler;

import com.dmurraysd.spring.logging.IdProvider;
import com.dmurraysd.spring.logging.LoggingUtil;
import com.dmurraysd.spring.service.LiveEventTrackerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

@Component
public class LiveMatchScoreScheduledTask {

    private static final String SOURCE_ID = "LIVE_MATCH_SCORE_SCHEDULED_TASK";

    private final LiveEventTrackerService liveEventTrackerService;
    private final Supplier<UUID> uuidSupplier;

    public LiveMatchScoreScheduledTask(final LiveEventTrackerService liveEventTrackerService,
                                       final Supplier<UUID> uuidSupplier) {
        this.liveEventTrackerService = liveEventTrackerService;
        this.uuidSupplier = uuidSupplier;
    }

    @Scheduled(fixedRateString = "${scheduled.job.fixed.delay}")
    public void publishLiveMatchScores() {

        final IdProvider context = LoggingUtil.toJobLoggingContext(uuidSupplier.get(), SOURCE_ID);

        liveEventTrackerService.publishLiveMatchScores(context);
    }
}
