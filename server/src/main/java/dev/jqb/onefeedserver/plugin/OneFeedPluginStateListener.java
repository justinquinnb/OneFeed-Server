package dev.jqb.onefeedserver.plugin;

import dev.jqb.onefeed.core.actor.OneFeedActor;
import dev.jqb.onefeed.core.caching.Cacher;
import dev.jqb.onefeed.core.caching.OneFeedCacherPlugin;
import dev.jqb.onefeed.core.content.OneFeedContent;
import dev.jqb.onefeed.core.provider.OneFeedProviderPlugin;
import dev.jqb.onefeedserver.aggregation.AggregationService;
import dev.jqb.onefeedserver.author.AuthorService;
import dev.jqb.onefeedserver.provider.ProviderRegistry;
import dev.jqb.onefeedserver.tasks.TaskRegistry;
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
    private final ProviderRegistry providerRegistry;
    private final TaskRegistry taskRegistry;
    private final AggregationService aggregationService;
    private final AuthorService authorService;

    @Autowired
    public OneFeedPluginStateListener(PluginTypeRegistry typeRegistry,
        ProviderRegistry providerRegistry, TaskRegistry taskRegistry,
        AggregationService aggregationService, AuthorService authorService
    ) {
        this.typeRegistry = typeRegistry;
        this.providerRegistry = providerRegistry;
        this.taskRegistry = taskRegistry;
        this.aggregationService = aggregationService;
        this.authorService = authorService;
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
                providerRegistry.register(plugin.getProvider());
            } else if (OneFeedCacherPlugin.class.isAssignableFrom(pluginClass)) {
                if (aggregationService.getCache() != null) {
                    throw new IllegalStateException("Cannot register multiple cachers");
                }

                OneFeedCacherPlugin plugin = (OneFeedCacherPlugin) wrapper.getPlugin();

                // TODO improve below lines if possible
                // Rn, this means things'll break if OFContent or OFActors aren't being used as the
                // normalized class. If a fork-er uses a different type then problems will arise :/
                aggregationService.setCache((Cacher<OneFeedContent, OneFeedActor>) plugin.getCacher());
                authorService.setCache((Cacher<OneFeedContent, OneFeedActor>) plugin.getCacher());
            }
        } else if (state == PluginState.STOPPED || state == PluginState.DISABLED ||
            state == PluginState.UNLOADED
        ) {
            typeRegistry.deregisterTypesFrom(wrapper.getPluginId());
            taskRegistry.deregisterPluginTasks(wrapper.getPluginId());

            Class<?> pluginClass = wrapper.getPlugin().getClass();
            if (OneFeedProviderPlugin.class.isAssignableFrom(pluginClass)) {
                providerRegistry.deregister(wrapper.getPluginId());
            } else if (OneFeedCacherPlugin.class.isAssignableFrom(pluginClass)) {
                aggregationService.setCache(null);
                authorService.setCache(null);
            }
        }
    }
}
