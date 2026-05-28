package dev.jqb.onefeed.app.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides {@code .env} configurations
 */
@Configuration
public class EnvConfig {

    @Bean
    Dotenv dotenv() {
        return Dotenv.load();
    }
}
