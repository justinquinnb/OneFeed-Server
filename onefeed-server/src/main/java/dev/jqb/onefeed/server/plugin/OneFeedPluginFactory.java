package dev.jqb.onefeed.server.plugin;

import dev.jqb.onefeed.core.provider.OneFeedProviderPlugin;
import dev.jqb.onefeed.core.provider.ProviderConfig;
import dev.jqb.onefeed.core.plugin.PluginConfigsFile;
import java.lang.reflect.Constructor;
import org.pf4j.DefaultPluginFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OneFeed's custom plugin factory, providing plugin-specific env variables to the plugin classes
 * themselves
 */
public class OneFeedPluginFactory extends DefaultPluginFactory {
    private static final Logger logger = LoggerFactory.getLogger(OneFeedPluginFactory.class);

    /**
     * The complete set of environment variables OneFeed has been started with,
     */
    private final PluginConfigsFile pluginConfigsFile;

    public OneFeedPluginFactory(PluginConfigsFile pluginConfigsFile) {
        super();
        this.pluginConfigsFile = pluginConfigsFile;
    }

    @Override
    protected Plugin createInstance(Class<?> pluginClass, PluginWrapper pluginWrapper) {
        try {
            // Pass the provider env map to the class if it's a plugin provider
            if (OneFeedProviderPlugin.class.isAssignableFrom(pluginClass)) {
                ProviderConfig pluginEnv = pluginConfigsFile.getProviderConfigs()
                    .get(pluginWrapper.getPluginId());

                Constructor<?> constructor = pluginClass.getConstructor(String.class, ProviderConfig.class);
                return (Plugin) constructor.newInstance(pluginWrapper.getPluginId(), pluginEnv);
            }

            Constructor<?> constructor = pluginClass.getConstructor(String.class);
            return (Plugin) constructor.newInstance(pluginWrapper.getPluginId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
