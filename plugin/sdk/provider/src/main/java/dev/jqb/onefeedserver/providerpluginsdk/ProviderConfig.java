package dev.jqb.onefeedserver.providerpluginsdk;

import dev.jqb.onefeed.core.actor.OneFeedActor;
import dev.jqb.onefeed.core.content.OneFeedContent;
import java.util.HashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A provider plugin's specific configuration, including those specific to any of its feeds
 */
@Setter
@NoArgsConstructor
public class ProviderConfig {

    /**
     * Whether to use the "lite" fetch mode, wherein the provider only requests the fields of
     * authors and content that are required to complete {@link OneFeedContent} and
     * {@link OneFeedActor} objects during normalization.
     */
    @Getter
    protected boolean useLiteFetchMode = true;

    /**
     * Provider-specific configuration of arbitrary shape
     */
    @Getter
    private HashMap<String, Object> topLevelConfig;

    /**
     * A mapping of arbitrary feed names to arbitrarily shaped, feed-specific configuration data
     */
    @Getter
    private HashMap<String, HashMap<String, Object>> feedConfigs;

    /**
     * Creates a new {@code ProviderEnv} object with the given {@code pluginVars} and {@code feedVars}
     *
     * @param pluginConfig plugin-specific configuration of arbitrary shape
     * @param feedConfigs a mapping of arbitrary feed names to arbitrarily shaped, feed-specific
     *              configuration data
     */
    public ProviderConfig(HashMap<String, Object> pluginConfig,
        HashMap<String, HashMap<String, Object>> feedConfigs
    ) {
        this.topLevelConfig = pluginConfig;
        this.feedConfigs = feedConfigs;
    }

    public boolean isUsingLiteFetchMode() {
        return useLiteFetchMode;
    }

    public void setUsingLiteFetchMode(boolean useLiteFetchMode) {
        this.useLiteFetchMode = useLiteFetchMode;
    }
}
