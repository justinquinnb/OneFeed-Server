package dev.jqb.onefeed.app.aggregation;

import dev.jqb.onefeed.api.aggregation.AggregationOptions;
import dev.jqb.onefeed.api.aggregation.Aggregator;
import dev.jqb.onefeed.api.caching.Cacher;
import dev.jqb.onefeed.api.content.Content;
import dev.jqb.onefeed.api.content.Normalizer;
import dev.jqb.onefeed.api.content.RawContent;
import dev.jqb.onefeed.api.feed.Feed;
import dev.jqb.onefeed.api.feed.Provider;
import dev.jqb.onefeed.api.impl.OneFeedContent;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Responsible for aggregating content from {@link Provider}s
 */
@Service
public class AggregationService implements Aggregator<OneFeedContent> {
    private static final Logger logger = LoggerFactory.getLogger(AggregationService.class);

    private final JsonMapper jsonMapper;

    /**
     * -- SETTER --
     *  Sets the service that this plugin uses to cache content.
     * @param cache the service that this plugin uses to cache and retrieve content
     */
    @Setter
    @Getter
    private Cacher cache;

    @Autowired
    public AggregationService(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public Flux<OneFeedContent> aggregate(int amount, List<Feed<? extends RawContent>> feeds,
        AggregationOptions options
    ) {
        HashMap<String, Integer> targetAmounts = options.getTargetAmounts(amount);
        List<Flux<OneFeedContent>> normalizedContentStreams = new ArrayList<>(feeds.size());

        for (Feed<? extends RawContent> feed : feeds) {
            Provider<? extends RawContent> provider = feed.getProvider();
            Normalizer<RawContent, OneFeedContent> normalizer =
                (Normalizer<RawContent, OneFeedContent>) provider.getNormalizer();
            String feedName = feed.getId().getName();

            Flux<? extends RawContent> feedStream = provider.fetchRecentContent(feedName,
                targetAmounts.get(feed.getId().toIdString()));

            normalizedContentStreams.add(
                feedStream
                    .map(normalizer::normalize)
                    .doOnError(err -> logger.warn(
                        "Error fetching content from feed \"{}\": {}", feedName, err.getStackTrace()))
                    .onErrorComplete()
            );
        }

        Flux<OneFeedContent> mergedFlux = Flux.merge(normalizedContentStreams);
        mergedFlux.doOnNext(this::cacheIfAble);

        return mergedFlux;
    }

    @Override
    public Flux<OneFeedContent> aggregate(int amount, List<Feed<? extends RawContent>> feeds,
        String aggregateCursor, AggregationOptions options
    ) {
        HashMap<String, Integer> targetAmounts = options.getTargetAmounts(amount);
        HashMap<String, String> cursors = decodeAggregateCursor(aggregateCursor);
        List<Flux<OneFeedContent>> normalizedContentStreams = new ArrayList<>(feeds.size());

        for (Feed<? extends RawContent> feed : feeds) {
            Provider<? extends RawContent> provider = feed.getProvider();
            Normalizer<RawContent, OneFeedContent> normalizer =
                (Normalizer<RawContent, OneFeedContent>) provider.getNormalizer();
            String feedName = feed.getId().getName();
            Flux<? extends RawContent> feedStream = provider.fetchRecentContent(feedName,
                targetAmounts.get(feed.getId().toIdString()), cursors.get(feedName));

            normalizedContentStreams.add(
                feedStream
                    .map(normalizer::normalize)
                    .doOnError(err -> logger.warn(
                        "Error fetching content from feed \"{}\": {}", feedName, err.getStackTrace()))
                    .onErrorComplete()
            );
        }

        Flux<OneFeedContent> mergedFlux = Flux.merge(normalizedContentStreams);
        mergedFlux.doOnNext(this::cacheIfAble);

        return mergedFlux;
    }

    /**
     * Caches the given content if the cache is set.
     * @param content the piece of {@link OneFeedContent} to cache if the cache is set
     */
    private void cacheIfAble(OneFeedContent content) {
        if (cache != null) {
            cache.cacheContent(List.of(content));
        }
    }

    @Override
    public String generateAggregateCursor(List<OneFeedContent> content) {
        // Feed name -> oldest content for that feed
        HashMap<String, Content> oldestFeedContent = new HashMap<>();

        // Feed name -> nextPageCursor string for that feed
        HashMap<String, String> cursorMap = new HashMap<>();

        // For all content in the aggregation list...
        for (OneFeedContent c : content) {
            String feedId = c.getSource().getFeedId().toIdString();

            // Associate the current content piece with the feed if no entry exists already
            Content currentOldest = oldestFeedContent.putIfAbsent(feedId, c);
            cursorMap.putIfAbsent(feedId, c.getNextPageCursor());

            // Else, as is the case when an entry already existed for that feed, replace it if
            // this content is older
            if (c.getPublished().isBefore(currentOldest.getPublished())) {
                oldestFeedContent.put(feedId, c);
                cursorMap.put(feedId, c.getNextPageCursor());
            }
        }

        // Then, serialize and encode it
        byte[] jsonBytes = jsonMapper.writeValueAsBytes(cursorMap);
        return Base64.getEncoder().encodeToString(jsonBytes);
    }

    @Override
    public HashMap<String, String> decodeAggregateCursor(String aggregateCursor) {
        String decoded = new String(Base64.getDecoder().decode(aggregateCursor));
        return jsonMapper.readValue(decoded, new TypeReference<HashMap<String, String>>() {});
    }
}
