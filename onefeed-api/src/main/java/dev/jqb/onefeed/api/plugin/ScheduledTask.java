package dev.jqb.onefeed.api.plugin;

import lombok.Getter;
import lombok.Setter;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A task to perform on a schedule
 */
@Getter
public sealed abstract class ScheduledTask implements Runnable permits CronTask, FixedDelayTask {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    /**
     * The task to perform
     */
    private Runnable task;

    /**
     * The human-friendly name of the task
     */
    private String name;

    /**
     * The plugin that requested the task
     */
    @Setter
    private PluginWrapper requester;

    public ScheduledTask(Runnable task, String name) {
        this.task = task;
        this.name = name;
    }

    @Override
    public void run() {
        logger.debug("Running scheduled task from '{}': {}", requester.getPluginId(), name);
        task.run();
    }
}
