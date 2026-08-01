package dev.jqb.onefeedserver.provider;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.provider.Provider;
import dev.jqb.onefeed.core.provider.ProviderIdentifiable;
import dev.jqb.onefeedserver.feed.FeedRegistry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A registry of provider IDs to the {@link Provider}s themselves
 *
 * @see ProviderIdentifiable
 */
@Component
public class ProviderRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ProviderRegistry.class);

    private final ConcurrentHashMap<String, Provider<? extends Content, ? extends Actor>> providers =
        new ConcurrentHashMap<>();

    public FeedRegistry feedRegistry;

    @Autowired
    public ProviderRegistry(FeedRegistry feedRegistry) {
        this.feedRegistry = feedRegistry;
    }

    /**
     * Register a provider.
     *
     * @param provider the provider instance
     */
    public void register(Provider<? extends Content, ? extends Actor> provider) {
        providers.put(provider.getId(), provider);
        feedRegistry.registerFeedsFor(provider);
        logger.debug("Registered provider: {}", provider.getId());
    }

    /**
     * Deregisters the provider with the given ID
     * @param providerId the ID of the provider to deregister
     */
    public void deregister(String providerId) {
        logger.debug("Deregistering provider: '{}'", providerId);
        providers.remove(providerId);
        feedRegistry.deregisterFeedsFor(providerId);
    }

    /**
     * Gets the provider for the given provider ID.
     * @param providerId the ID of the provider to retrieve
     * @return the provider for the given provider ID
     */
    public Optional<Provider<? extends Content, ? extends Actor>> getProvider(
        ProviderIdentifiable providerId
    ) {
        return Optional.ofNullable(providers.get(providerId.getProviderId()));
    }

    /**
     * Gets the provider for the given provider ID.
     * @param providerId the ID of the provider to retrieve
     * @return the provider for the given provider ID
     */
    public Optional<Provider<? extends Content, ? extends Actor>> getProvider(String providerId) {
        return Optional.ofNullable(providers.get(providerId));
    }
}
