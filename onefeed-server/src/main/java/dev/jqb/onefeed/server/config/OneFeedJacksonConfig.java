package dev.jqb.onefeed.server.config;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.server.plugin.PluginMixIn;
import dev.jqb.onefeed.server.plugin.PluginTypeRegistry;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tells Jackson how to handle external classes by mixing in an interface pointing to the
 * {@link PluginTypeRegistry}
 */
@Configuration
public class OneFeedJacksonConfig {
    @Bean
    public JsonMapperBuilderCustomizer jacksonHandlerCustomizer(
        AutowireCapableBeanFactory beanFactory
    ) {
        return builder -> {
            builder.addMixIn(Content.class, PluginMixIn.class);
            builder.addMixIn(Actor.class, PluginMixIn.class);
        };
    }
}
