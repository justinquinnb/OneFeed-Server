package dev.jqb.onefeed.api.plugin;

import java.time.Duration;
import lombok.Getter;

/**
 * A task to perform at a fixed delay
 */
@Getter
public final class FixedDelayTask extends ScheduledTask {

    /**
     * The delay between each execution of the task
     */
    private final Duration delay;

    /**
     * Creates a new runnable task to execute at the given, fixed {@code delay}.
     *
     * @param task the task to execute
     * @param name the human-friendly name of the task
     * @param delay the delay between each execution of the task
     */
    public FixedDelayTask(Runnable task, String name, Duration delay) {
        super(task, name);
        this.delay = delay;
    }
}
