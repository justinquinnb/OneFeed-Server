package dev.jqb.onefeed.app;

import dev.jqb.onefeed.api.plugin.ScheduledTasks;
import dev.jqb.onefeed.api.plugin.CronTask;
import dev.jqb.onefeed.api.plugin.FixedDelayTask;
import dev.jqb.onefeed.api.plugin.ScheduledTask;
import dev.jqb.onefeed.app.plugin.OneFeedPluginManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.pf4j.Plugin;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * Runs on application startup, with the Spring context already initialized
 */
@Component
public class StartupRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);
    private OneFeedPluginManager oneFeedPluginManager;
    private ThreadPoolTaskScheduler pluginThreadPoolTaskScheduler;

    @Autowired
    public StartupRunner(
        OneFeedPluginManager oneFeedPluginManager,
        ThreadPoolTaskScheduler pluginThreadPoolTaskScheduler
    ) {
        this.oneFeedPluginManager = oneFeedPluginManager;
        this.pluginThreadPoolTaskScheduler = pluginThreadPoolTaskScheduler;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initPlugins();
    }

    /**
     * Initialize plugins from the configured plugin directory.
     */
    private void initPlugins() {
        logger.info("Loading plugins from {}", oneFeedPluginManager.getPluginsRoots());
        oneFeedPluginManager.loadPlugins();
        String loadedPluginsList = oneFeedPluginManager.getPlugins().stream().map(plugin ->
            "\n\t" + plugin.getPluginId() +
                " (v" + plugin.getDescriptor().getVersion() + ") by "
                + plugin.getDescriptor().getProvider()
        ).collect(Collectors.joining());

        if (oneFeedPluginManager.getPlugins().isEmpty()) {
            logger.warn("No plugins found");
            return;
        }

        logger.info("{} plugins loaded", oneFeedPluginManager.getPlugins().size());
        logger.debug("Loaded plugins: {}", loadedPluginsList);

        logger.info("Starting plugins up...");
        oneFeedPluginManager.startPlugins();
        logger.info("All plugins started.");

        // Find all scheduled tasks
        logger.info("Initializing scheduled plugin tasks...");
        List<ScheduledTask> discoveredTasks = findScheduledPluginTasks(this.oneFeedPluginManager);

        // Schedule all the tasks
        scheduleTasks(pluginThreadPoolTaskScheduler, discoveredTasks);
        logger.info("All scheduled tasks initialized.");

        logger.info("OneFeed is ready to serve!");
    }

    /**
     * Finds all scheduled tasks from plugins the {@code oneFeedPluginManager} has loaded
     * @param oneFeedPluginManager the plugin manager to search plugins with
     *
     * @return a list of all the tasks plugins desire scheduling for
     */
    private List<ScheduledTask> findScheduledPluginTasks(PluginManager oneFeedPluginManager) {
        logger.debug("Finding plugin tasks to schedule...");
        List<ScheduledTask> collectedTasks = new ArrayList<>();

        // Find all plugins implementing scheduled tasks
        for (PluginWrapper pluginWrapper : oneFeedPluginManager.getPlugins()) {
            Plugin plugin = pluginWrapper.getPlugin();
            if (plugin instanceof ScheduledTasks) {
                List<ScheduledTask> pluginTasks = ((ScheduledTasks) plugin).getScheduledTasks();

                // Collect all the plugin's tasks
                for (ScheduledTask task : pluginTasks) {
                    task.setRequester(pluginWrapper);
                    logger.debug("Found task for '{}': {}", pluginWrapper.getPluginId(), task.getName());
                    collectedTasks.add(task);
                }
            }
        }

        return collectedTasks;
    }

    /**
     * Schedules all the {@code tasks} in the given list for execution by the provided
     * {@code scheduler}.
     *
     * @param scheduler the scheduler to hand the {@code tasks} to for scheduling and execution
     * @param tasks the tasks to hand over to the {@code scheduler}
     */
    private void scheduleTasks(ThreadPoolTaskScheduler scheduler, List<ScheduledTask> tasks) {
        logger.debug("Scheduling {} tasks...", tasks.size());
        // Now schedule the tasks for that plugin
        for (ScheduledTask task : tasks) {
            // Determine the type of scheduling it requires
            String humanTriggerName = "";
            if (task instanceof FixedDelayTask) {
                Duration delay = ((FixedDelayTask) task).getDelay();
                humanTriggerName = delay.toString();
                scheduler.scheduleAtFixedRate(task, delay);
            } else if (task instanceof CronTask) {
                String cronExpression = ((CronTask) task).getCronExpression();
                CronTrigger cronTrigger = new CronTrigger(cronExpression);
                humanTriggerName = cronExpression;
                scheduler.schedule(task, cronTrigger);
            } else {
                logger.warn("Skipping unsupported task type: {}", task.getClass().getSimpleName());
                continue;
            }

            logger.debug("Scheduled task {} with trigger '{}'", task.getName(), humanTriggerName);
        }
    }
}
