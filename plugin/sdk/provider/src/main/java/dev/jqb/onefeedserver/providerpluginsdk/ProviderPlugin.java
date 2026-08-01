package dev.jqb.onefeedserver.providerpluginsdk;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.provider.Provider;
import dev.jqb.onefeedserver.pluginsdk.core.OneFeedPlugin;

/**
 * A OneFeed {@link Provider} plugin
 */
public abstract class ProviderPlugin extends OneFeedPlugin {
    protected ProviderConfig providerConfig;

    /**
     * Constructs a new {@link OneFeedPlugin} with the given {@code providerEnv}.
     * @param pluginId the unique identifier of this plugin in the app context
     * @param providerConfig the environment variables specific to this plugin
     */
    protected ProviderPlugin(String pluginId, ProviderConfig providerConfig) {
        super(pluginId);
        this.providerConfig = providerConfig;
    }

    /**
     * Gets the content {@link Provider} that this plugin... well, provides.
     * @return the content {@link Provider} that this plugin provides
     */
    public abstract Provider<? extends Content, ? extends Actor> getProvider();
}
