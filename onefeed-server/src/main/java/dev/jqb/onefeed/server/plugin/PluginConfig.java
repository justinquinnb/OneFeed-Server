package dev.jqb.onefeed.server.plugin;

import dev.jqb.onefeed.core.plugin.PluginConfigsFile;
import dev.jqb.onefeed.server.aggregation.FeedRegistry;
import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.pf4j.PluginStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.dataformat.yaml.YAMLMapper;

@Configuration
@ConfigurationProperties("onefeed.plugins")
@Getter
@Setter
public class PluginConfig {
    private String directoryPath;
    private String pluginConfigPath;

    private static final Logger logger = LoggerFactory.getLogger(PluginConfig.class);

    @Bean
    public PluginConfigsFile pluginConfigsFile(Dotenv dotEnv) {
        Path envMapFile = Path.of(pluginConfigPath);

        try {
            logger.debug("Reading provider envs file: {}", envMapFile.toAbsolutePath());
            String envMapStr = Files.readString(envMapFile);

            // Replace all .env variable references
            String preparedEnvMapStr = replaceEnvReferences(envMapStr, dotEnv);

            logger.debug("Deserializing {}'s prepared contents...", envMapFile.getFileName());
            YAMLMapper mapper = YAMLMapper.builder().build();
            return mapper.readValue(preparedEnvMapStr, PluginConfigsFile.class);
        } catch (Exception e) {
            throw new IllegalStateException(
                "Failed to load plugin configurations from file: " + envMapFile.getFileName(), e
            );
        }
    }

    @Bean
    public OneFeedPluginManager oneFeedPluginManager(PluginConfigsFile pluginConfigsFile,
        PluginTypeRegistry pluginTypeRegistry, FeedRegistry feedRegistry,
        PluginStateListener pluginStateListener
    ) {
        OneFeedPluginManager pluginManager = new OneFeedPluginManager(Path.of(directoryPath),
            pluginConfigsFile);

        // Register the listener with the plugin manager
        pluginManager.addPluginStateListener(pluginStateListener);

        return pluginManager;
    }

    /**
     * Replaces all instances of {@code ${VAR_NAME}} in the given {@code envMap}'s values with
     * the corresponding value from OneFeed's environment.
     *
     * @param providerConfigsString the raw YAML string containing {@code .env} file references
     * @param dotEnv the actual environment variables OneFeed is running with from {@code .env}
     */
    private static String replaceEnvReferences(String providerConfigsString, Dotenv dotEnv) {
        logger.debug("Replacing values of .env references in provider env map...");
        Pattern referencePattern = Pattern.compile("\\$\\{.*}");
        Matcher matcher = referencePattern.matcher(providerConfigsString);
        return matcher.replaceAll(match -> getEnvVarFromReference(match, dotEnv));
    }

    /**
     * Parses the given {@code .env} value {@code reference} and returns the actual value from the
     * {@code dotEnv} object.
     *
     * @param reference the reference to the value in the {@code .env} file to get
     * @param dotEnv the actual environment variables OneFeed is running with from {@code .env}
     * @return the value of the {@code .env} variable referenced by {@code reference}
     */
    private static String getEnvVarFromReference(MatchResult reference, Dotenv dotEnv) {
        String envKey = reference.group().substring(2, reference.group().length() - 1);

        if (envKey.isEmpty()) {
            throw new IllegalArgumentException("Reference to .env in provider envs YAML cannot be empty");
        }

        String envVal = dotEnv.get(envKey);
        Objects.requireNonNull(envVal, "Environment variable " + envKey +
            " is referenced in the provider envs YAML, but is null in .env");

        logger.trace("Replaced reference to {} in .env file with actual value", envKey);
        return envVal;
    }
}
