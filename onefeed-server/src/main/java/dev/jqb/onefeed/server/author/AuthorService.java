package dev.jqb.onefeed.server.author;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.actor.ActorKey;
import dev.jqb.onefeed.core.actor.ActorTransformer;
import dev.jqb.onefeed.core.actor.OneFeedActor;
import dev.jqb.onefeed.core.caching.Cacher;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.content.OneFeedContent;
import dev.jqb.onefeed.core.provider.Provider;
import dev.jqb.onefeed.server.provider.ProviderRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private Cacher<OneFeedContent, OneFeedActor> cache;

    private ProviderRegistry providerRegistry;

    @Autowired
    public AuthorService(ProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    /**
     * Gets the desired author
     * @param authorKey the key of the author to retrieve
     * @return the desired author
     */
    public Mono<OneFeedActor> getAuthor(ActorKey authorKey) {
        Optional<OneFeedActor> cachedAuthor = fetchFromCacheIfAble(authorKey);
        if (cachedAuthor.isPresent()) {
            return Mono.just(cachedAuthor.get());
        }

        Optional<Provider<? extends Content, ? extends Actor>> possibleProvider =
            providerRegistry.getProvider(authorKey);
        if (possibleProvider.isEmpty()) {
            logger.warn("No provider found for author {}", authorKey);
            return Mono.empty();
        }
        Provider<? extends Content, ? extends Actor> provider = possibleProvider.get();
        ActorTransformer<Actor, OneFeedActor> actorNormalizer =
            (ActorTransformer<Actor, OneFeedActor>) provider.getActorNormalizer();

        return provider
            .fetchAuthor(authorKey.idOnPlatform())
            .map(actorNormalizer::transform)
            .doOnNext(this::cacheIfAble)
            .doOnError(err -> logger.warn(
                "Error fetching author '{}': {}", authorKey, err.getStackTrace()))
            .onErrorComplete();
    }

    /**
     * Gets the authors from the desired keys
     * @param authorKeys the keys of the authors to retrieve
     * @return a stream of {@link Actor}s as they arrive from their platforms' API
     */
    public Flux<OneFeedActor> getAuthors(List<ActorKey> authorKeys) {
        List<Mono<OneFeedActor>> normalizedAuthorMonos = new ArrayList<>();
        for (ActorKey authorKey : authorKeys) {
            normalizedAuthorMonos.add(getAuthor(authorKey));
        }

        return Flux.merge(normalizedAuthorMonos);
    }

    /**
     * Caches the given author if the cache is set.
     * @param author the {@link Actor} to cache if the cache is set
     */
    private void cacheIfAble(OneFeedActor author) {
        if (cache != null) {
            cache.cacheAuthor(author);
        }
    }

    private Optional<OneFeedActor> fetchFromCacheIfAble(ActorKey authorKey) {
        if (cache == null) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(cache.fetchAuthor(authorKey));
        } catch (Exception e) {
            logger.error("Error fetching author from cache", e);
            return Optional.empty();
        }
    }
}
