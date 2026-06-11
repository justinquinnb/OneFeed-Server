package dev.jqb.onefeed.server.plugin;

import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.plugin.OneFeedPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Type registry for Jackson to use when deserializing custom {@link Content} types, whether that
 * be from database/cache responses or otherwise
 */
@Component
public class PluginTypeRegistry {
    private static final Logger logger = LoggerFactory.getLogger(PluginTypeRegistry.class);

    private final Map<String, Class<?>> fqnToTypes = new ConcurrentHashMap<>();
    private final Map<String, List<String>> pluginIdToFqns = new ConcurrentHashMap<>();

    /**
     * Register all types that a plugin may need to deserialize from JSON strings.
     * @param wrapper the wrapper of the plugin whose types to register
     */
    public void registerTypesFrom(PluginWrapper wrapper) {
        String pluginId = wrapper.getPluginId();
        logger.debug("Registering types for plugin '{}' to support later deserialization",
            pluginId);

        OneFeedPlugin plugin = (OneFeedPlugin) wrapper.getPlugin();

        for (Class<?> customClass : plugin.getClassesToDeserialize()) {
            String fqn = customClass.getName();
            fqnToTypes.put(fqn, customClass);

            // Add the FQN to the list of FQNs for this plugin
            pluginIdToFqns.computeIfAbsent(pluginId, k -> new ArrayList<>())
                .add(fqn);

            logger.trace("Registered type '{}'", customClass.getName());
        }
    }

    /**
     * Deregister all types that a plugin may have needed to deserialize from JSON strings.
     * @param pluginId the ID of the plugin whose types to deregister
     */
    public void deregisterTypesFrom(String pluginId) {
        logger.debug("Deregistering types for plugin '{}'", pluginId);
        List<String> fqns = pluginIdToFqns.remove(pluginId);
        if (fqns != null) {
            for (String fqn : fqns) {
                fqnToTypes.remove(fqn);
                logger.trace("Deregistered type '{}'", fqn);
            }
        }
    }

    /**
     * Resolve a class from its fully qualified class name.
     * @param fqn the fully qualified class name
     * @return the class, if it exists
     */
    public Class<?> resolve(String fqn) {
        return fqnToTypes.get(fqn);
    }
}
