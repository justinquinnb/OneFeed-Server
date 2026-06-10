package dev.jqb.onefeed.app.aggregation;

import dev.jqb.onefeed.api.aggregation.AggregateCursorGenerator;
import dev.jqb.onefeed.api.aggregation.Aggregation;
import dev.jqb.onefeed.api.aggregation.AggregationOptions;
import dev.jqb.onefeed.api.author.PlatformAuthor;
import dev.jqb.onefeed.api.content.Content;
import dev.jqb.onefeed.api.content.NormalizedContent;
import dev.jqb.onefeed.api.content.PlatformContent;
import dev.jqb.onefeed.api.author.Author;
import dev.jqb.onefeed.api.feed.Feed;
import dev.jqb.onefeed.api.feed.FeedIdentifier;
import dev.jqb.onefeed.api.impl.OneFeedContent;
import dev.jqb.onefeed.api.impl.OneFeedCursor;
import dev.jqb.onefeed.api.impl.OneFeedAuthor;
import dev.jqb.onefeed.app.author.AuthorService;
import dev.jqb.onefeed.app.model.StreamedAuthor;
import dev.jqb.onefeed.app.model.StreamedContent;
import dev.jqb.onefeed.app.model.StreamedCursor;
import dev.jqb.onefeed.app.model.CustomAggregation;
import dev.jqb.onefeed.app.model.CustomAggregation.WeightedFeed;
import dev.jqb.onefeed.app.model.StreamData;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Endpoints to get aggregations of content from multiple feeds
 */
@RestController
@Validated
@RequestMapping("/aggregation")
@Tag(name = "Aggregation", description = "Endpoints for aggregating content from multiple feeds")
public class AggregationController implements AggregateCursorGenerator<OneFeedContent> {
    private static final Logger logger = LoggerFactory.getLogger(AggregationController.class);

    private final JsonMapper jsonMapper;
    private final AggregationService aggregationService;
    private final AuthorService authorService;
    private final FeedRegistry feedRegistry;

    @Autowired
    public AggregationController(AggregationService aggregationService, AuthorService authorService,
        FeedRegistry feedRegistry, JsonMapper jsonMapper
    ) {
        this.aggregationService = aggregationService;
        this.authorService = authorService;
        this.feedRegistry = feedRegistry;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Gets a stream of content and authors representing the desired data from the given feeds.
     *
     * @param amount the total amount of content to retrieve
     * @param customAggregation the combination of feed IDs and optional weights to use in the
     *                          aggregation
     * @param includeAuthors whether to include the authors of the aggregated content
     *                       (optional, defaults to {@code true})
     * @param aggregateCursor the point to start retrieving content after, inclusively (optional)
     *
     * @return a stream of content and authors representing the desired data from the given feeds,
     * emitted as soon as it's available
     */
    @PostMapping("/stream/custom")
    public Flux<StreamData> getCustomAggregationStream(
        @RequestParam @Min(1) int amount,
        @RequestBody @Valid CustomAggregation customAggregation,
        @RequestParam(defaultValue = "true") Boolean includeAuthors,
        @RequestParam(required = false) String aggregateCursor
    ) {
        // Get the feed IDs first
        List<FeedIdentifier> ids = customAggregation.getWeightedFeeds().stream().map(wf ->
            FeedIdentifier.fromIdString(wf.getFeedId())).toList();

        // Try to get the associated feeds, if the IDs are valid
        List<Feed<? extends PlatformContent, ? extends PlatformAuthor>> feeds =
            new ArrayList<>(ids.size());
        for (FeedIdentifier id : ids) {
            Feed<? extends PlatformContent, ? extends PlatformAuthor> feed =
                feedRegistry.getFeed(id);
            feeds.add(feed);
        }

        // Convert the weights to the map required by the aggregator
        HashMap<FeedIdentifier, Integer> weights = new HashMap<>();
        for (WeightedFeed fw : customAggregation.getWeightedFeeds()) {
            if (fw.getWeight() == null) {
                weights.put(FeedIdentifier.fromIdString(fw.getFeedId()), 1);
            } else {
                weights.put(FeedIdentifier.fromIdString(fw.getFeedId()), fw.getWeight());
            }
        }

        // Get the content stream
        AggregationOptions aggOptions = new AggregationOptions(weights);
        Flux<OneFeedContent> contentStream;

        if (aggregateCursor != null && !aggregateCursor.isBlank()) {
            Map<FeedIdentifier, OneFeedCursor> cursors = decodeAggregateCursor(aggregateCursor);
            contentStream = aggregationService.aggregate(amount, feeds, cursors, aggOptions);
        } else {
            contentStream = aggregationService.aggregate(amount, feeds, aggOptions);
        }

        // Collect all the content for aggregate cursor generation
        List<OneFeedContent> allContent = new ArrayList<>();

        Flux<StreamedContent<OneFeedContent>> contentUpdateStream = contentStream
            .doOnNext(allContent::add)
            .map(StreamedContent::new);

        // Optionally get the author stream
        Flux<StreamedAuthor> authorUpdateStream;
        if (includeAuthors) {
            authorUpdateStream = authorService.getAuthors(feeds).map(StreamedAuthor::new);
        } else {
            authorUpdateStream = Flux.empty();
        }

        return Flux.merge(contentUpdateStream, authorUpdateStream).concatWith(
            Mono.fromCallable(() -> new StreamedCursor(generateAggregateCursor(allContent)))
        );
    }

    /**
     * Gets a complete aggregation of the desired amount of content from the given feeds.
     *
     * @param amount the total amount of content to retrieve
     * @param customAggregation the combination of feed IDs and optional weights to use in the
     *                          aggregation
     * @param includeAuthors whether to include the authors of the aggregated content
     *                       (optional, defaults to {@code true})
     * @param aggregateCursor the point to start retrieving content after, inclusively (optional)
     *
     * @return complete, structured aggregation data of the desired amount of content from the given
     * feeds
     */
    @PostMapping("/batch/custom")
    public Aggregation getCustomAggregationBatch(
        @RequestParam @Min(1) int amount,
        @RequestBody @Valid CustomAggregation customAggregation,
        @RequestParam(defaultValue = "true") Boolean includeAuthors,
        @RequestParam(required = false) String aggregateCursor
    ) {
        Flux<StreamData> stream =
            getCustomAggregationStream(amount, customAggregation, includeAuthors, aggregateCursor);
        List<StreamData> streamData = stream.collectList().block();

        List<NormalizedContent> content = new ArrayList<>();
        Map<FeedIdentifier, Author> authors = new HashMap<>();
        String aggregateCursorStr = null;

        // Organize the data
        for (StreamData streamDataObj : streamData) {
            switch (streamDataObj) {
                case StreamedContent<?> cu:
                    content.add(cu.getContent());
                    break;
                case StreamedAuthor au:
                    authors.put(au.getAuthor().getFeedIdentifier(), au.getAuthor());
                    break;
                case StreamedCursor cu:
                    aggregateCursorStr = cu.getAggregateCursor();
                    break;
                default:
                    break;
            }
        }

        // Build the aggregation object
        return new Aggregation(authors, content, aggregateCursorStr);
    }

    /**
     * Gets a preconfigured aggregation of the desired amount of content after the given cursor.
     *
     * @param id the ID of the preconfigured aggregation to retrieve
     * @param aggregateCursor the point to start retrieving content after, inclusively (optional)
     * @return the preconfigured aggregation
     */
    @GetMapping("/stream/preset/{id}")
    public Flux<StreamData> getPreconfiguredAggregationStream(
        @PathVariable @NotBlank String id,
        @RequestParam @Min(1) int amount,
        @RequestParam(required = false) String aggregateCursor
    ) {
        // TODO implement
        return Flux.empty();
    }

    @Override
    public String generateAggregateCursor(List<OneFeedContent> content) {
        List<OneFeedContent> sortedContent = new ArrayList<>(content);
        sortedContent.sort(Content::compareTo);

        HashMap<FeedIdentifier, OneFeedCursor> oldestFeedCursors = new HashMap<>();
        HashMap<FeedIdentifier, OneFeedCursor> cursors = new HashMap<>();

        // Because the content is in descending timestamp order, the last piece of content with a
        // cursor for a feed is easy to get with this
        for (OneFeedContent c : sortedContent) {
            // First piece of content in list for feed
            if (!oldestFeedCursors.containsKey(c.getFeedIdentifier())) {
                OneFeedCursor combinedCursor = new OneFeedCursor(c.getNextPageCursor(),
                    0, c.getSource().getIdOnPlatform());
                oldestFeedCursors.put(c.getFeedIdentifier(), combinedCursor);
                continue;
            }

            // Nth piece of content in feed
            // Piece of content has no next page cursor
            OneFeedCursor currentCursor = oldestFeedCursors.get(c.getFeedIdentifier());
            if (c.getNextPageCursor() == null) {
                int currentOffset = currentCursor.getOffsetFromCursor();
                currentCursor.setOffsetFromCursor(currentOffset + 1);
            } else { // Piece of content HAS a next page cursor
                currentCursor.setOffsetFromCursor(0);
                currentCursor.setCursorOnPlatform(c.getNextPageCursor());
            }

            // The latest piece of content accessed for a feed is guaranteed to be the oldest for
            // that feed because of the sort, so we should always update the ID (not necessary for
            // first if statement bc ID is set there as-is)
            currentCursor.setIdOnPlatform(c.getSource().getIdOnPlatform());
        }

        byte[] jsonBytes = jsonMapper.writeValueAsBytes(oldestFeedCursors);
        return Base64.getEncoder().encodeToString(jsonBytes);
    }

    @Override
    public Map<FeedIdentifier, OneFeedCursor> decodeAggregateCursor(String aggregateCursor) {
        try {
            String decoded = new String(Base64.getDecoder().decode(aggregateCursor));
            return jsonMapper.readValue(decoded,
                new TypeReference<Map<FeedIdentifier, OneFeedCursor>>() {});
        } catch (Exception e) {
            throw new MalformedAggregateCursorException();
        }
    }
}
