package dev.jqb.onefeed.api.feed;

import dev.jqb.onefeed.api.content.RawContent;
import dev.jqb.onefeed.api.plugin.OneFeedPlugin;
import java.util.List;

/**
 * A OneFeed {@link Provider} plugin
 */
public abstract class OneFeedProviderPlugin extends OneFeedPlugin {
    /**
     * Constructs a new {@link OneFeedPlugin} with the given {@code providerEnv}.
     * @param providerConfig the environment variables specific to this plugin
     */
    protected OneFeedProviderPlugin(ProviderConfig providerConfig) {
        super();
    }

    /**
     * Gets the content {@link Provider} that this plugin... well, provides.
     * @return the content {@link Provider} that this plugin provides
     */
    public abstract Provider<? extends RawContent> getProvider();

    /**
     * Gets the names of the feeds that this plugin provides.
     * @return a list of the names of the feeds that this plugin is configured to provide
     */
    public abstract List<String> getFeedNames();
}
