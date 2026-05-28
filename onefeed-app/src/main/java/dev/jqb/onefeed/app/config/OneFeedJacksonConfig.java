package dev.jqb.onefeed.app.config;

import dev.jqb.onefeed.api.content.Content;
import dev.jqb.onefeed.api.feed.Author;
import dev.jqb.onefeed.app.plugin.PluginMixIn;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.support.JacksonHandlerInstantiator;

/**
 * Tells Jackson to search for our custom
 */
@Configuration
public class OneFeedJacksonConfig {
    @Bean
    public JsonMapperBuilderCustomizer jacksonHandlerCustomizer(
        AutowireCapableBeanFactory beanFactory) {
        return builder -> {
            builder.handlerInstantiator(new JacksonHandlerInstantiator(beanFactory));
            builder.addMixIn(Content.class, PluginMixIn.class);
            builder.addMixIn(Author.class, PluginMixIn.class);
        };
    }
}
