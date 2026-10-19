package org.example.productchecker.scheduler;

import jakarta.annotation.PostConstruct;
import org.example.productchecker.service.ProductCheckerService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

@Component
public class MonitoringScheduler {

    private final ProductCheckerService productCheckerService;
    private final DelayGenerator delayGenerator;
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> future;

    public MonitoringScheduler(ProductCheckerService productCheckerService, DelayGenerator delayGenerator, TaskScheduler taskScheduler) {
        this.productCheckerService = productCheckerService;
        this.delayGenerator = delayGenerator;
        this.taskScheduler = taskScheduler;
    }

    @PostConstruct
    public void start() {
        scheduleNext();
    }

    private void scheduleNext() {
        Duration delay = delayGenerator.nextCycleDelay(productCheckerService.getFailuresInRow());
        future = taskScheduler.schedule(() -> {
            productCheckerService.runCheck();
            scheduleNext();
        }, Instant.now().plus(delay));
    }
}
