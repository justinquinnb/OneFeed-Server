package dev.jqb.onefeedserver.pluginsdk.core;

import dev.jqb.onefeed.core.caching.CacherConfig;
import dev.jqb.onefeed.core.provider.ProviderConfig;
import java.util.HashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A type corresponding to the mostly known shape of the plugin configuration file, used for its
 * parsing. Allows plugins to reference environment variables for arbitrarily named feeds
 * with static keys and no concern for naming conflicts.
 */
@Getter
@Setter
@NoArgsConstructor
public class PluginConfigsFile {

    /**
     * A mapping of provider plugin IDs to their specific "environment" variables
     */
    private HashMap<String, ProviderConfig> providerConfigs;

    /**
     * A mapping of cacher plugin IDs to their specific "environment" variables
     */
    private HashMap<String, CacherConfig> cacherConfigs;

    /**
     * Creates a new {@link PluginConfigsFile} object with the given {@code data}
     * @param providerConfigs the provider config data from the plugin config YAML file
     * @param cacherConfigs the cacher config data from the plugin config YAML file
     */
    public PluginConfigsFile(HashMap<String, ProviderConfig> providerConfigs,
        HashMap<String, CacherConfig> cacherConfigs
    ) {
        this.providerConfigs = providerConfigs;
        this.cacherConfigs = cacherConfigs;
    }
}
