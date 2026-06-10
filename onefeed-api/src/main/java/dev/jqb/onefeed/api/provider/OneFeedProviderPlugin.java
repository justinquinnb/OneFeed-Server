package dev.jqb.onefeed.api.provider;

import dev.jqb.onefeed.api.author.PlatformAuthor;
import dev.jqb.onefeed.api.content.PlatformContent;
import dev.jqb.onefeed.api.plugin.OneFeedPlugin;
import java.util.List;

/**
 * A OneFeed {@link Provider} plugin
 */
public abstract class OneFeedProviderPlugin extends OneFeedPlugin {
    protected ProviderConfig providerConfig;

    /**
     * Constructs a new {@link OneFeedPlugin} with the given {@code providerEnv}.
     * @param pluginId the unique identifier of this plugin in the app context
     * @param providerConfig the environment variables specific to this plugin
     */
    protected OneFeedProviderPlugin(String pluginId, ProviderConfig providerConfig) {
        super(pluginId);
        this.providerConfig = providerConfig;
    }

    /**
     * Gets the content {@link Provider} that this plugin... well, provides.
     * @return the content {@link Provider} that this plugin provides
     */
    public abstract Provider<? extends PlatformContent, ? extends PlatformAuthor> getProvider();

    /**
     * Gets the names of the feeds that this plugin provides.
     * @return a list of the names of the feeds that this plugin is configured to provide
     */
    public abstract List<String> getFeedNames();
}
