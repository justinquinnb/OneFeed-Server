package dev.jqb.onefeedserver.cacherpluginsdk;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeedserver.pluginsdk.core.OneFeedPlugin;

public abstract class CacherPlugin extends OneFeedPlugin {
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
    protected CacherPlugin(String pluginId, CacherConfig cacherConfig) {
        super(pluginId);
        this.cacherConfig = cacherConfig;
    }

    /**
     * Gets the {@link Cacher} service that this plugin provides.
     * @return the {@link Cacher} service that this plugin provides
     */
    public abstract Cacher<? extends Content, ? extends Actor> getCacher();
}
