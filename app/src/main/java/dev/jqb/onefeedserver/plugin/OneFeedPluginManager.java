package dev.jqb.onefeedserver.plugin;

import dev.jqb.onefeedserver.cacherpluginsdk.CacherPlugin;
import dev.jqb.onefeedserver.providerpluginsdk.ProviderPlugin;
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
     * Gets a list of all {@link PluginState#RESOLVED} {@link ProviderPlugin}s.
     * @return a list of all {@link PluginState#RESOLVED} {@link ProviderPlugin}s
     */
    public List<PluginWrapper> getProviders() {
        return getPlugins().stream()
            .filter(p -> p.getPlugin() instanceof ProviderPlugin).toList();
    }

    /**
     * Gets the first {@link PluginState#RESOLVED} {@link CacherPlugin} found in the plugin
     * manager.
     * @return the first {@link PluginState#RESOLVED} {@link CacherPlugin} found in the
     * plugin manager
     */
    public PluginWrapper getCacher() {
        return getPlugins().stream()
            .filter(p -> p.getPlugin() instanceof CacherPlugin)
            .toList().getFirst();
    }
}
