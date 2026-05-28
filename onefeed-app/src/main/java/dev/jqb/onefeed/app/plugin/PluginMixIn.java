package dev.jqb.onefeed.app.plugin;

import tools.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * A Jackson mix-in for plugins so the API submodule doesn't depend on this app module to provide
 * the {@link PluginTypeIdResolver}
 */
@JsonTypeIdResolver(PluginTypeIdResolver.class)
public interface PluginMixIn {}
