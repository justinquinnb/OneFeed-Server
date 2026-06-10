package dev.jqb.onefeed.app.author;

import dev.jqb.onefeed.api.caching.Cacher;
import dev.jqb.onefeed.api.feed.Author;
import dev.jqb.onefeed.api.feed.Feed;
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
    public Flux<Author> getAuthors(List<Feed<?>> feeds) {
        // Try to get the associated feeds, if the IDs are valid
        List<Mono<? extends Author>> authorMonos = new ArrayList<>(feeds.size());
        for (Feed<?> feed : feeds) {
            Provider<?> provider = feed.getProvider();
            Mono<? extends Author> authorMono = provider.fetchProfile(feed.getId().getFeedName());
            authorMono.doOnError(err -> logger.warn(
                    "Error fetching author from feed '{}': {}", feed.getId().getFeedName(),
                    err.getStackTrace()))
                .onErrorComplete();
            authorMonos.add(authorMono);
        }

        return Flux.merge(authorMonos).doOnNext(this::cacheIfAble);
    }

    /**
     * Caches the given author if the cache is set.
     * @param author the {@link Author} to cache if the cache is set
     */
    private void cacheIfAble(Author author) {
        if (cache != null) {
            cache.cacheContent(List.of(author));
        }
    }
}
