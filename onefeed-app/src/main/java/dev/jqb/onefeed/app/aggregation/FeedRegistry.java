package dev.jqb.onefeed.app.aggregation;

import dev.jqb.onefeed.api.content.PlatformContent;
import dev.jqb.onefeed.api.feed.Feed;
import dev.jqb.onefeed.api.feed.FeedIdentifier;
import dev.jqb.onefeed.api.feed.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A registry of feed names to their providers
 */
@Component
public class FeedRegistry {
    private static final Logger logger = LoggerFactory.getLogger(FeedRegistry.class);

    private final ConcurrentHashMap<String, Provider<?>> feedIdToProvider = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> pluginIdToFeedNames = new ConcurrentHashMap<>();

    /**
     * Register all feeds that a provider plugin is responsible for.
     *
     * @param wrapper the wrapper of the provider plugin whose feeds to register
     * @param provider the provider plugin instance
     * @param feedNames the names of the feeds that the plugin is responsible for
     */
    public void registerFeedsFor(PluginWrapper wrapper, Provider<?> provider,
        List<String> feedNames
    ) {
        String pluginId = wrapper.getPluginId();
        logger.debug("Associating feed names with provider instance \"{}\"...",
            pluginId);

        for (String feedName : feedNames) {
            FeedIdentifier id = new FeedIdentifier(pluginId, feedName);
            feedIdToProvider.put(id.toIdString(), provider);
            pluginIdToFeedNames.computeIfAbsent(pluginId, k -> new ArrayList<>())
                .add(feedName);
            logger.trace("Associated feed \"{}\"", feedName);
        }
    }

    /**
     * Deregister all feeds that a provider plugin is responsible for.
     * @param wrapper the wrapper of the provider plugin whose feeds to deregister
     */
    public void deregisterFeedsFor(PluginWrapper wrapper) {
        String pluginId = wrapper.getPluginId();
        logger.debug("Unregistering feeds for plugin \"{}\"", pluginId);
        List<String> feedNames = pluginIdToFeedNames.remove(pluginId);
        if (feedNames != null) {
            for (String feedName : feedNames) {
                FeedIdentifier id = new FeedIdentifier(pluginId, feedName);
                feedIdToProvider.remove(id);
                logger.trace("Unregistered feed \"{}\"", feedName);
            }
        }
    }

    /**
     * Gets the provider for the given feed ID.
     * @param feedId the ID of the feed whose provider to retrieve
     * @return the provider for the given feed ID or {@code null} if no such provider exists
     *
     * @see FeedIdentifier#toIdString()
     */
    public Provider<?> getProvider(FeedIdentifier feedId) {
        return feedIdToProvider.get(feedId.toIdString());
    }

    /**
     * Gets the feed for the given feed ID.
     * @param feedId the ID of the feed whose provider to retrieve
     * @return the feed for the given feed ID or {@code null} if no such feed exists
     *
     * @see FeedIdentifier#toIdString()
     */
    public Feed<? extends PlatformContent> getFeed(FeedIdentifier feedId) {
        return new Feed<>(feedId, getProvider(feedId));
    }
}
