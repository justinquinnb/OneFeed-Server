package dev.jqb.onefeed.app.tasks;

import dev.jqb.onefeed.app.StartupRunner;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A registry of the tasks associated with each plugin
 */
@Component
public class TaskRegistry {
    private static final Logger logger = LoggerFactory.getLogger(TaskRegistry.class);

    /**
     * A mapping of plugin IDs to their associated tasks (if any)
     */
    private ConcurrentHashMap<String, List<ScheduledFuture<?>>> pluginIdToTasks =
        new ConcurrentHashMap<>();

    /**
     * Register all tasks associated with a plugin ID
     * @param pluginId the ID of the plugin whose tasks to register
     * @param tasks the tasks to register
     */
    public void registerPluginTasks(String pluginId, List<ScheduledFuture<?>> tasks) {
        pluginIdToTasks.put(pluginId, tasks);
    }

    /**
     * Get all tasks associated with a plugin ID
     * @param pluginId the ID of the plugin whose tasks to retrieve
     * @return the tasks associated with the given plugin ID, or an empty list if none are
     * registered
     */
    public List<ScheduledFuture<?>> getPluginTasks(String pluginId) {
        return pluginIdToTasks.get(pluginId);
    }

    /**
     * Cancel and deregister all tasks associated with a plugin ID
     * @param pluginId the ID of the plugin whose tasks to cancel
     */
    public void deregisterPluginTasks(String pluginId) {
        logger.debug("Cancelling tasks for plugin '{}'", pluginId);
        for (ScheduledFuture<?> task : pluginIdToTasks.remove(pluginId)) {
            task.cancel(true);
        }
    }
}
