package dev.jqb.onefeed.api.provider;

import dev.jqb.onefeed.api.impl.OneFeedAuthor;
import dev.jqb.onefeed.api.impl.OneFeedContent;
import java.util.HashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A provider plugin's specific configuration, including those specific to any of its feeds
 */
@Getter
@Setter
@NoArgsConstructor
public class ProviderConfig {

    /**
     * Whether to use the "lite" fetch mode, wherein the provider only requests the fields of
     * authors and content that are required to complete {@link OneFeedContent} and
     * {@link OneFeedAuthor} objects during normalization.
     */
    private boolean useLiteFetchMode = true;

    /**
     * Plugin-specific configuration of arbitrary shape
     */
    private HashMap<String, Object> pluginVars;

    /**
     * A mapping of arbitrary feed names to arbitrarily shaped, feed-specific configuration data
     */
    private HashMap<String, HashMap<String, Object>> feeds;

    /**
     * Creates a new {@code ProviderEnv} object with the given {@code pluginVars} and {@code feedVars}
     *
     * @param pluginVars plugin-specific configuration of arbitrary shape
     * @param feeds a mapping of arbitrary feed names to arbitrarily shaped, feed-specific
     *              configuration data
     */
    public ProviderConfig(HashMap<String, Object> pluginVars,
        HashMap<String, HashMap<String, Object>> feeds
    ) {
        this.pluginVars = pluginVars;
        this.feeds = feeds;
    }

    public boolean isUsingLiteFetchMode() {
        return useLiteFetchMode;
    }

    public void setUsingLiteFetchMode(boolean useLiteFetchMode) {
        this.useLiteFetchMode = useLiteFetchMode;
    }
}
