package dev.jqb.onefeed.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Provides the task scheduler bean, which is used to handle execution of
 * {@link dev.jqb.onefeed.core.plugin.ScheduledTask}s
 */
@Configuration
public class ThreadPoolTaskSchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler pluginThreadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("PluginThreadPoolTaskScheduler");
        return scheduler;
    }
}
