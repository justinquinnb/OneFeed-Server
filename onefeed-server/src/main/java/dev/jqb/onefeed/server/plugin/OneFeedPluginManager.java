package dev.jqb.onefeed.server.plugin;

import dev.jqb.onefeed.core.caching.OneFeedCacherPlugin;
import dev.jqb.onefeed.core.provider.OneFeedProviderPlugin;
import dev.jqb.onefeed.core.plugin.PluginConfigsFile;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;

/**
 * OneFeed's custom plugin manager
 */
public class OneFeedPluginManager extends DefaultPluginManager {

    public OneFeedPluginManager(Path pluginsPath, PluginConfigsFile pluginConfigsFile) {
        super(pluginsPath);
        Objects.requireNonNull(pluginConfigsFile, "pluginConfigsFile arg must not be null");
        this.pluginFactory = new OneFeedPluginFactory(pluginConfigsFile);
    }

    /**
     * Gets a list of all {@link PluginState#RESOLVED} {@link OneFeedProviderPlugin}s.
     * @return a list of all {@link PluginState#RESOLVED} {@link OneFeedProviderPlugin}s
     */
    public List<PluginWrapper> getProviders() {
        return getPlugins().stream()
            .filter(p -> p.getPlugin() instanceof OneFeedProviderPlugin).toList();
    }

    /**
     * Gets the first {@link PluginState#RESOLVED} {@link OneFeedCacherPlugin} found in the plugin
     * manager.
     * @return the first {@link PluginState#RESOLVED} {@link OneFeedCacherPlugin} found in the
     * plugin manager
     */
    public PluginWrapper getCacher() {
        return getPlugins().stream()
            .filter(p -> p.getPlugin() instanceof OneFeedCacherPlugin)
            .toList().getFirst();
    }
}
