package dev.jqb.onefeed.app.plugin;

import dev.jqb.onefeed.api.caching.OneFeedCacherPlugin;
import dev.jqb.onefeed.api.feed.OneFeedProviderPlugin;
import dev.jqb.onefeed.app.aggregation.AggregationService;
import dev.jqb.onefeed.app.aggregation.FeedRegistry;
import dev.jqb.onefeed.app.tasks.TaskRegistry;
import org.pf4j.PluginState;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginStateListener;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Listener for OneFeed plugin state changes
 */
@Component
public class OneFeedPluginStateListener implements PluginStateListener {
    private final PluginTypeRegistry typeRegistry;
    private final FeedRegistry feedRegistry;
    private final TaskRegistry taskRegistry;
    private final AggregationService aggregationService;

    @Autowired
    public OneFeedPluginStateListener(PluginTypeRegistry typeRegistry, FeedRegistry feedRegistry,
        TaskRegistry taskRegistry, AggregationService aggregationService
    ) {
        this.typeRegistry = typeRegistry;
        this.feedRegistry = feedRegistry;
        this.taskRegistry = taskRegistry;
        this.aggregationService = aggregationService;
    }

    @Override
    public void pluginStateChanged(PluginStateEvent event) {
        // Only register types when the plugin is actually ready
        PluginState state = event.getPluginState();
        PluginWrapper wrapper = event.getPlugin();
        if (state == PluginState.STARTED) {
            typeRegistry.registerTypesFrom(wrapper);

            Class<?> pluginClass = wrapper.getPlugin().getClass();
            if (OneFeedProviderPlugin.class.isAssignableFrom(pluginClass)) {
                OneFeedProviderPlugin plugin = (OneFeedProviderPlugin) wrapper.getPlugin();
                feedRegistry.registerFeedsFor(wrapper, plugin.getProvider(), plugin.getFeedNames());
            } else if (OneFeedCacherPlugin.class.isAssignableFrom(pluginClass)) {
                if (aggregationService.getCache() != null) {
                    throw new IllegalStateException("Cannot register multiple cachers");
                }

                OneFeedCacherPlugin plugin = (OneFeedCacherPlugin) wrapper.getPlugin();
                aggregationService.setCache(plugin.getCacher());
            }
        } else if (state == PluginState.STOPPED || state == PluginState.DISABLED ||
            state == PluginState.UNLOADED
        ) {
            typeRegistry.deregisterTypesFrom(wrapper.getPluginId());
            taskRegistry.deregisterPluginTasks(wrapper.getPluginId());

            Class<?> pluginClass = wrapper.getPlugin().getClass();
            if (OneFeedProviderPlugin.class.isAssignableFrom(pluginClass)) {
                feedRegistry.deregisterFeedsFor(wrapper);
            } else if (OneFeedCacherPlugin.class.isAssignableFrom(pluginClass)) {
                aggregationService.setCache(null);
            }
        }
    }
}
