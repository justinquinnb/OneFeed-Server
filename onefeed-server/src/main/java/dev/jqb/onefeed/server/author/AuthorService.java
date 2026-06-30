package dev.jqb.onefeed.server.author;

import dev.jqb.onefeed.core.caching.Cacher;
import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.feed.Feed;
import dev.jqb.onefeed.core.actor.OneFeedActor;
import dev.jqb.onefeed.core.provider.Provider;
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
     * @return a stream of {@link Actor}s as they arrive from their platforms' API
     */
    public Flux<OneFeedActor> getAuthors(
        List<Feed<? extends PlatformContent, ? extends PlatformActor>> feeds
    ) {
        // Try to get the associated feeds, if the IDs are valid
        List<Mono<? extends OneFeedActor>> normalizedAuthorMonos = new ArrayList<>(feeds.size());

        for (Feed<? extends PlatformContent, ? extends PlatformActor> feed : feeds) {
            Provider<? extends PlatformContent, ? extends PlatformActor> provider = feed.getProvider();
            Mono<? extends PlatformActor> authorMono = provider.fetchAuthor(feed.getId().getFeedName());
            ActorNormalizer<PlatformActor, OneFeedActor> actorNormalizer =
                (ActorNormalizer<PlatformActor, OneFeedActor>) provider.getAuthorNormalizer();

            normalizedAuthorMonos.add(
                authorMono
                    .map(actorNormalizer::normalize)
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
     * @param author the {@link Actor} to cache if the cache is set
     */
    private void cacheIfAble(OneFeedActor author) {
        if (cache != null) {
            cache.cacheContent(List.of(author));
        }
    }
}
