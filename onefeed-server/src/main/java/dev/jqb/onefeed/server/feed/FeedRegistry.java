package dev.jqb.onefeed.server.feed;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.feed.Feed;
import dev.jqb.onefeed.core.feed.FeedId;
import dev.jqb.onefeed.core.feed.FeedIdentifiable;
import dev.jqb.onefeed.core.provider.Provider;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A registry of {@link FeedId}s to the {@link Feed}s themselves.
 */
@Component
public class FeedRegistry {
    private static final Logger logger = LoggerFactory.getLogger(FeedRegistry.class);

    private final ConcurrentHashMap<FeedId, Feed<? extends Content>> feeds =
        new ConcurrentHashMap<>();

    /**
     * Register all feeds that a provider is responsible for.
     *
     * @param provider the provider whose feeds to register
     */
    public void registerFeedsFor(Provider<? extends Content, ? extends Actor> provider) {
        logger.debug("Registering feeds for provider: '{}'", provider.getId());
        for (Feed<? extends Content> feed : provider.getFeeds()) {
            this.feeds.put(feed.getId(), feed);
        }
    }

    /**
     * Deregisters all the feeds that a provider is responsible for.
     * @param providerId the provider whose feeds to deregister
     */
    public void deregisterFeedsFor(String providerId) {
        logger.debug("Deregistering feeds from provider: '{}'", providerId);
        for (FeedId feedId : feeds.keySet()) {
            if (feedId.getProviderId().equals(providerId)) {
                feeds.remove(feedId);
            }
        }
    }

    /**
     * Gets the feed for the given feed ID.
     * @param feedId the ID of the feed to retrieve
     * @return the feed for the given feed ID
     */
    public Optional<Feed<? extends Content>> getFeed(FeedIdentifiable feedId) {
        return Optional.ofNullable(feeds.get(feedId.getFeedId()));
    }
}
