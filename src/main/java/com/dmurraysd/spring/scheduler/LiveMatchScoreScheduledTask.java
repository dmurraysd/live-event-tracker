package com.dmurraysd.spring.scheduler;

import com.dmurraysd.spring.logging.IdProvider;
import com.dmurraysd.spring.logging.LoggingUtil;
import com.dmurraysd.spring.service.LiveEventTrackerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

import static com.dmurraysd.spring.logging.LoggingUtil.formatLogMessage;

@Component
public class LiveMatchScoreScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(LiveMatchScoreScheduledTask.class);
    private static final String SOURCE_ID = "LIVE_MATCH_SCORE_SCHEDULED_TASK";

    private final LiveEventTrackerService liveEventTrackerService;
    private final Supplier<UUID> uuidSupplier;

    public LiveMatchScoreScheduledTask(final LiveEventTrackerService liveEventTrackerService,
                                       final Supplier<UUID> uuidSupplier) {
        this.liveEventTrackerService = liveEventTrackerService;
        this.uuidSupplier = uuidSupplier;
    }

    @Scheduled(fixedRateString = "${scheduled.job.fixed.delay}", initialDelayString = "${scheduled.job.fixed.delay}")
    public void publishLiveMatchScores() {
        final IdProvider context = LoggingUtil.toJobLoggingContext(uuidSupplier.get(), SOURCE_ID);
        logger.info(formatLogMessage(context, "Publishing live match scores scheduled task commencing"));

        liveEventTrackerService.publishLiveMatchScores(context);
    }
}
