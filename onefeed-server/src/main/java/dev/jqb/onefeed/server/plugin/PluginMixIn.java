package dev.jqb.onefeed.server.plugin;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * A Jackson mix-in for plugins so the API submodule doesn't depend on this app module to provide
 * the {@link PluginTypeIdResolver}
 */
@JsonTypeIdResolver(PluginTypeIdResolver.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface PluginMixIn {}
