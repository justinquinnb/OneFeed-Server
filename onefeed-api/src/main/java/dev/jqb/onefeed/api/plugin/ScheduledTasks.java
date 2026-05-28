package dev.jqb.onefeed.api.plugin;

import java.util.List;

/**
 * A class that has recurring tasks for the Spring task scheduler to manage
 */
public interface ScheduledTasks {

    /**
     * Gets all scheduled tasks from the implementor.
     * @return all scheduled tasks from the implementor, in no specific order
     */
    List<ScheduledTask> getScheduledTasks();
}
