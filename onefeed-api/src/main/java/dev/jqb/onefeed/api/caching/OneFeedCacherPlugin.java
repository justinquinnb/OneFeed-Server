package dev.jqb.onefeed.api.caching;

import dev.jqb.onefeed.api.author.NormalizedAuthor;
import dev.jqb.onefeed.api.content.NormalizedContent;
import dev.jqb.onefeed.api.plugin.OneFeedPlugin;

public abstract class OneFeedCacherPlugin extends OneFeedPlugin {
    /**
     * The environment variables specific to this plugin, as specified in OneFeed's
     * {@code plugin-env.yaml} and {@code .env} files.
     */
    protected CacherConfig cacherConfig;

    /**
     * Constructs a new {@link OneFeedPlugin} with the given {@code cacherEnv}.
     * @param pluginId the unique identifier of this plugin in the app context
     * @param cacherConfig the environment variables specific to this plugin
     *
     * @see #cacherConfig
     */
    protected OneFeedCacherPlugin(String pluginId, CacherConfig cacherConfig) {
        super(pluginId);
        this.cacherConfig = cacherConfig;
    }

    /**
     * Gets the {@link Cacher} service that this plugin provides.
     * @return the {@link Cacher} service that this plugin provides
     */
    public abstract Cacher<? extends NormalizedContent, ? extends NormalizedAuthor> getCacher();
}
