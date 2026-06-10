package dev.jqb.onefeed.api.plugin;

import dev.jqb.onefeed.api.provider.Provider;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.pf4j.Plugin;

/**
 * A plugin for OneFeed, representing the package containing a {@link Provider} implementation
 */
@Getter
@Setter
public abstract class OneFeedPlugin extends Plugin {

    /**
     * The unique identifier of this plugin in the app context
     */
    protected String pluginId;

    /**
     * Constructs a new {@code OneFeedPlugin} with the given plugin ID
     * @param pluginId the unique identifier of this plugin in the app context
     */
    public OneFeedPlugin(String pluginId) {
        this.pluginId = pluginId;
    }

    /**
     * Gets all classes that OneFeed may need to deserialize from JSON strings.
     *
     * @return a list of classes that OneFeed may need to deserialize from JSON strings, such as
     * a content or author type that will be cached
     */
    public List<Class<?>> getClassesToDeserialize() {
        return List.of();
    }
}
