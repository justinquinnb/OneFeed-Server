package dev.jqb.onefeedserver.pluginsdk.test;

import dev.jqb.onefeedserver.pluginsdk.core.OneFeedPlugin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

/**
 * The root of OneFeed plugin tests, providing a shared, initialized plugin instance of the
 * target type to all tests
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class OneFeedPluginTests<T extends OneFeedPlugin> {
    protected T plugin;

    /**
     * Get an initialized instance of the plugin to test.
     */
    protected abstract T getInitializedPlugin();

    @BeforeAll
    void setSharedPlugin() {
        this.plugin = getInitializedPlugin();

        if (this.plugin == null) {
            throw new IllegalStateException("Plugin instance must be provided.");
        }
    }
}
