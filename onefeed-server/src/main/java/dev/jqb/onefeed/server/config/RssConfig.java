package dev.jqb.onefeed.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Conf
 */
@Configuration
@ConfigurationProperties("onefeed.rss")
@Getter
@Setter
public class RssConfig {
    private int maxChannelItems = 10;
}
