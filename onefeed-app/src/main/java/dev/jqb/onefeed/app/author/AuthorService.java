package dev.jqb.onefeed.app.author;

import dev.jqb.onefeed.api.author.AuthorNormalizer;
import dev.jqb.onefeed.api.author.PlatformAuthor;
import dev.jqb.onefeed.api.caching.Cacher;
import dev.jqb.onefeed.api.author.Author;
import dev.jqb.onefeed.api.content.ContentNormalizer;
import dev.jqb.onefeed.api.content.PlatformContent;
import dev.jqb.onefeed.api.feed.Feed;
import dev.jqb.onefeed.api.impl.OneFeedAuthor;
import dev.jqb.onefeed.api.impl.OneFeedContent;
import dev.jqb.onefeed.api.provider.Provider;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Responsible for retrieving author data from {@link Provider}s
 */
@Service
public class AuthorService {
    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);

    /**
     * -- SETTER --
     *  Sets the service that this plugin uses to cache content.
     * @param cache the service that this plugin uses to cache and retrieve content
     */
    @Setter
    @Getter
    private Cacher cache;

    /**
     * Gets the authors of the given feeds.
     * @param feeds the {@link Feed}s whose authors to retrieve
     * @return a stream of {@link Author}s as they arrive from their platforms' API
     */
    public Flux<OneFeedAuthor> getAuthors(
        List<Feed<? extends PlatformContent, ? extends PlatformAuthor>> feeds
    ) {
        // Try to get the associated feeds, if the IDs are valid
        List<Mono<? extends OneFeedAuthor>> normalizedAuthorMonos = new ArrayList<>(feeds.size());

        for (Feed<? extends PlatformContent, ? extends PlatformAuthor> feed : feeds) {
            Provider<? extends PlatformContent, ? extends PlatformAuthor> provider = feed.getProvider();
            Mono<? extends PlatformAuthor> authorMono = provider.fetchAuthor(feed.getId().getFeedName());
            AuthorNormalizer<PlatformAuthor, OneFeedAuthor> authorNormalizer =
                (AuthorNormalizer<PlatformAuthor, OneFeedAuthor>) provider.getAuthorNormalizer();

            normalizedAuthorMonos.add(
                authorMono
                    .map(authorNormalizer::normalize)
                    .doOnError(err -> logger.warn(
                        "Error fetching author from feed '{}': {}", feed.getId().getFeedName(),
                        err.getStackTrace()))
                    .onErrorComplete()
            );
        }

        return Flux.merge(normalizedAuthorMonos).doOnNext(this::cacheIfAble);
    }

    /**
     * Caches the given author if the cache is set.
     * @param author the {@link Author} to cache if the cache is set
     */
    private void cacheIfAble(OneFeedAuthor author) {
        if (cache != null) {
            cache.cacheContent(List.of(author));
        }
    }
}
