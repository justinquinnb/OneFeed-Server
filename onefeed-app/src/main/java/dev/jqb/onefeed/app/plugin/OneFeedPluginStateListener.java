package dev.jqb.onefeed.app.plugin;

import dev.jqb.onefeed.api.feed.OneFeedProviderPlugin;
import dev.jqb.onefeed.app.aggregation.FeedRegistry;
import org.pf4j.PluginState;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginStateListener;
import org.pf4j.PluginWrapper;

/**
 * Listener for OneFeed plugin state changes
 */
public class OneFeedPluginStateListener implements PluginStateListener {
    private final PluginTypeRegistry typeRegistry;
    private final FeedRegistry feedRegistry;

    public OneFeedPluginStateListener(PluginTypeRegistry typeRegistry, FeedRegistry feedRegistry) {
        this.typeRegistry = typeRegistry;
        this.feedRegistry = feedRegistry;
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
            }
        } else if (state == PluginState.STOPPED || state == PluginState.DISABLED ||
            state == PluginState.UNLOADED
        ) {
            typeRegistry.unregisterTypesFrom(wrapper);

            Class<?> pluginClass = wrapper.getPlugin().getClass();
            if (OneFeedProviderPlugin.class.isAssignableFrom(pluginClass)) {
                feedRegistry.unregisterFeedsFor(wrapper);
            }
        }
    }
}
