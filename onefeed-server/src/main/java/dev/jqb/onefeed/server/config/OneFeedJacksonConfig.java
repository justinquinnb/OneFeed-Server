package dev.jqb.onefeed.server.config;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.server.plugin.PluginMixIn;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tells Jackson to search for our custom
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
