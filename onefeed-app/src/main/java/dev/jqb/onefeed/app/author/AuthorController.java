package dev.jqb.onefeed.app.author;

import dev.jqb.onefeed.api.author.NormalizedAuthor;
import dev.jqb.onefeed.api.author.PlatformAuthor;
import dev.jqb.onefeed.api.content.PlatformContent;
import dev.jqb.onefeed.api.author.Author;
import dev.jqb.onefeed.api.feed.Feed;
import dev.jqb.onefeed.api.feed.FeedIdentifier;
import dev.jqb.onefeed.app.aggregation.FeedRegistry;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Endpoints to retrieve author data for feeds
 */
@RestController
@Validated
@RequestMapping("/author")
@Tag(name = "Author", description = "Endpoints for retrieving feeds' author data")
public class AuthorController {
    private static final Logger logger = LoggerFactory.getLogger(AuthorController.class);

    private final AuthorService authorService;
    private final FeedRegistry feedRegistry;

    @Autowired
    public AuthorController(AuthorService authorService, FeedRegistry feedRegistry) {
        this.authorService = authorService;
        this.feedRegistry = feedRegistry;
    }

    /**
     * Gets a stream of author data for the desired feeds.
     * @param feedIds the IDs of the feeds whose authors to retrieve
     * @return a stream of author data as each feed's object arrives from their platform's
     * API
     */
    @GetMapping("/stream")
    public Flux<? extends NormalizedAuthor> getAuthorStream(
        @RequestParam @Size(min = 1) List<String> feedIds
    ) {
        List<FeedIdentifier> parsedFeedIds = feedIds.stream().map(FeedIdentifier::fromIdString)
            .toList();

        List<Feed<?, ? extends PlatformAuthor>> feeds = new ArrayList<>(parsedFeedIds.size());
        for (FeedIdentifier id : parsedFeedIds) {
            Feed<?, ? extends PlatformAuthor> feed = feedRegistry.getFeed(id);
            feeds.add(feed);
        }

        return authorService.getAuthors(feeds);
    }

    /**
     * Gets a complete list of author data for the desired feeds.
     * @param feedIds the IDs of the feeds whose authors to retrieve
     * @return a complete list of author data as each feed's object arrives from their
     * platform's
     * API
     */
    @GetMapping("/batch")
    public List<? extends NormalizedAuthor> getAuthorBatch(
        @RequestParam @Size(min = 1) List<String> feedIds
    ) {
        return getAuthorStream(feedIds).collectList().block();
    }
}
