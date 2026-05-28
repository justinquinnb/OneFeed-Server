package dev.jqb.onefeed.api.caching;

import java.util.HashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A cache plugin's specific configuration
 */
@Getter
@Setter
@NoArgsConstructor
public class CacherConfig {

    /**
     * Plugin-specific configuration of arbitrary shape
     */
    private HashMap<String, Object> pluginVars;

    /**
     * Content caching-specific configuration of arbitrary shape
     */
    private HashMap<String, Object> contentVars;

    /**
     * Author caching-specific configuration of arbitrary shape
     */
    private HashMap<String, Object> authorVars;

    /**
     * Creates a new {@code CacherEnv} object with the given data
     *
     * @param pluginVars plugin-specific configuration of arbitrary shape
     * @param contentVars content caching-specific configuration of arbitrary shape
     * @param authorVars author caching-specific configuration of arbitrary shape
     */
    public CacherConfig(HashMap<String, Object> pluginVars,
        HashMap<String, Object> contentVars, HashMap<String, Object> authorVars
    ) {
        this.pluginVars = pluginVars;
        this.contentVars = contentVars;
        this.authorVars = authorVars;
    }
}