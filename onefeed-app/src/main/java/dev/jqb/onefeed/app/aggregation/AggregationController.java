package dev.jqb.onefeed.app.aggregation;

import dev.jqb.onefeed.api.aggregation.AggregationOptions;
import dev.jqb.onefeed.api.content.RawContent;
import dev.jqb.onefeed.api.feed.Feed;
import dev.jqb.onefeed.api.feed.FeedIdentifier;
import dev.jqb.onefeed.api.impl.OneFeedContent;
import dev.jqb.onefeed.api.impl.Profile;
import dev.jqb.onefeed.app.model.AuthorUpdate;
import dev.jqb.onefeed.app.model.ContentUpdate;
import dev.jqb.onefeed.app.model.CustomAggregationDto;
import dev.jqb.onefeed.app.model.StreamData;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

@RestController
@Validated
@RequestMapping("/aggregation")
public class AggregationController {
    private static final Logger logger = LoggerFactory.getLogger(AggregationController.class);

    private final AggregationService aggregationService;
    private final FeedRegistry feedRegistry;

    @Autowired
    public AggregationController(AggregationService aggregationService, FeedRegistry feedRegistry) {
        this.aggregationService = aggregationService;
        this.feedRegistry = feedRegistry;
    }

    /**
     * Gets an aggregation of the desired amount of content from the given feeds.
     *
     * @param amount the total amount of content to retrieve
     * @param customAggregation the combination of feed IDs and optional weights to use in the
     *                          aggregation
     * @param includeAuthors whether to include the authors of the aggregated content
     *                       (optional, defaults to {@code true})
     * @param cursor the point to start retrieving content after, inclusively (optional)
     * @param dedupe whether to dedupe the content in the aggregation (optional, defaults to
     * {@code true})
     *
     * @return an aggregation of the desired amount of content from the given feeds
     */
    @PostMapping("/custom")
    public Flux<StreamData> getCustomAggregation(
        @RequestParam int amount,
        @RequestBody @Valid CustomAggregationDto customAggregation,
        @RequestParam(defaultValue = "true") Boolean includeAuthors,
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "true") Boolean dedupe
    ) {
        // Get the feed IDs first
        List<FeedIdentifier> ids = customAggregation.getFeedWeights().stream().map(fw ->
            FeedIdentifier.fromIdString(fw.getFeedId())).toList();

        // Try to get the associated feeds, if the IDs are valid
        List<Feed<? extends RawContent>> feeds = new ArrayList<>(ids.size());
        for (FeedIdentifier id : ids) {
            Feed<? extends RawContent> feed = feedRegistry.getFeed(id);
            if (feed == null) {
                throw new IllegalArgumentException("Invalid feed ID: " + id);
            }

            feeds.add(feed);
        }

        // Convert the weights to the map required by the aggregator
        HashMap<String, Integer> weights = new HashMap<>();
        for (CustomAggregationDto.FeedWeight fw : customAggregation.getFeedWeights()) {
            if (fw.getWeight() == null) {
                weights.put(fw.getFeedId(), 1);
            } else {
                weights.put(fw.getFeedId(), fw.getWeight());
            }
        }

        // Get the content stream
        AggregationOptions aggOptions = new AggregationOptions(dedupe, weights);
        Flux<ContentUpdate<OneFeedContent>> contentStream = aggregationService.aggregate(
            amount, feeds, aggOptions
        ).map(ContentUpdate::new);

        // Optionally get the author stream
        Flux<AuthorUpdate> authorStream;
        if (includeAuthors) {
            List<Mono<Profile>> authorMonos = new ArrayList<>(feeds.size());

            for (Feed<? extends RawContent> feed : feeds) {
                authorMonos.add(feed.getProvider().getProfile(feed.getId().getName()));
            }

            authorStream = Flux.merge(authorMonos).map(AuthorUpdate::new);
        } else {
            authorStream = Flux.empty();
        }

        return Flux.merge(contentStream, authorStream);
    }

    /**
     * Gets a preconfigured aggregation of the desired amount of content after the given cursor.
     *
     * @param id the ID of the preconfigured aggregation to retrieve
     * @param cursor the point to start retrieving content after, inclusively (optional)
     * @return the preconfigured aggregation
     */
    @GetMapping("/preconfigured/{id}")
    public Flux<StreamData> getPreconfiguredAggregation(
        @PathVariable String id,
        @RequestParam int amount,
        @RequestParam(required = false) String cursor
    ) {
        // TODO implement
        return Flux.empty();
    }
}
