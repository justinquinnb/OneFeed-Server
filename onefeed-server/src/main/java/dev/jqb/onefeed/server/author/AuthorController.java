package dev.jqb.onefeed.server.author;

import dev.jqb.onefeed.core.actor.ActorKey;
import dev.jqb.onefeed.server.response.StreamElement;
import dev.jqb.onefeed.server.response.std.OneFeedActorResponse;
import dev.jqb.onefeed.server.response.std.StdResponseMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Endpoints to retrieve author data from platforms
 */
@RestController
@Validated
@RequestMapping("/authors")
@Tag(name = "Author", description = "Endpoints for retrieving author data")
public class AuthorController {
    private final AuthorService authorService;
    private final StdResponseMapper responseMapper;

    @Autowired
    public AuthorController(AuthorService authorService, StdResponseMapper responseMapper) {
        this.authorService = authorService;
        this.responseMapper = responseMapper;
    }

    /**
     * Gets the requested author using its unique key.
     * @param authorKey the ID of the author to retrieve
     * @return the author data as it arrives from its platform's API
     *
     * @see ActorKey
     */
    @GetMapping("/{authorKey}")
    public Mono<OneFeedActorResponse> getAuthor(@PathVariable String authorKey) {
        return authorService.getAuthor(ActorKey.fromKeyString(authorKey))
            .map(responseMapper::toOneFeedActorResponse);
    }

    /**
     * Gets a stream of the desired authors.
     * @param authorKeys the keys of the authors to retrieve
     * @return a stream of author data as each object arrives from its platform's API
     *
     * @see ActorKey
     */
    @GetMapping("/stream")
    public Flux<StreamElement<OneFeedActorResponse>> getAuthorsStream(
        @RequestParam @Size(min = 1) List<String> authorKeys
    ) {
        List<ActorKey> actorKeys = authorKeys.stream().map(ActorKey::fromKeyString).toList();
        Set<ActorKey> uniqueKeys = Set.copyOf(actorKeys);
        return authorService.getAuthors(uniqueKeys)
            .map(responseMapper::toOneFeedActorResponse)
            .map(StreamElement::new);
    }

    /**
     * Gets a map of the desired authors' IDs to their data.
     * @param authorKeys the keys of the authors to retrieve
     * @return a map of the {@code authorKey}s to their respective {@code author}s
     *
     * @see ActorKey
     */
    @GetMapping("/map")
    public Map<ActorKey, OneFeedActorResponse> getAuthorsMap(
        @RequestParam @Size(min = 1) List<String> authorKeys
    ) {
        List<StreamElement<OneFeedActorResponse>> authors = getAuthorsStream(authorKeys).collectList().block();
        Map<ActorKey, OneFeedActorResponse> authorMap = new HashMap<>();
        for (StreamElement<OneFeedActorResponse> authorElement : authors) {
            OneFeedActorResponse author = authorElement.getData();
            authorMap.put(new ActorKey(author.providerId(), author.externalRef().id()), author);
        }
        return authorMap;
    }
}
