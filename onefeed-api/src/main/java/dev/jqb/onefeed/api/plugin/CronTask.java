package dev.jqb.onefeed.api.plugin;

import lombok.Getter;

/**
 * A task to perform on a cron schedule
 */
@Getter
public final class CronTask extends ScheduledTask {

    /**
     * The cron schedule to run the task on
     */
    private String cronExpression;

    /**
     * Creates a new runnable task to execute on the given cron schedule.
     *
     * @param task the task to execute
     * @param name the human-friendly name of the task
     * @param cronExpression the cron schedule to run the task on
     */
    public CronTask(Runnable task, String name, String cronExpression) {
        super(task, name);
        this.cronExpression = cronExpression;
    }
}
