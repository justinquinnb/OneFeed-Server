package dev.jqb.onefeed.server.author;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.actor.ActorKey;
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

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     * Gets the requested author using its unique key.
     * @param authorKey the ID of the author to retrieve
     * @return the author data as it arrives from its platform's API
     *
     * @see ActorKey
     */
    @GetMapping("/{authorKey}")
    public Mono<? extends Actor> getAuthor(@PathVariable String authorKey) {
        return authorService.getAuthor(ActorKey.fromKeyString(authorKey));
    }

    /**
     * Gets a stream of the desired authors.
     * @param authorKeys the keys of the authors to retrieve
     * @return a stream of author data as each object arrives from its platform's API
     *
     * @see ActorKey
     */
    @GetMapping("/stream")
    public Flux<? extends Actor> getAuthorsStream(
        @RequestParam @Size(min = 1) List<String> authorKeys
    ) {
        List<ActorKey> actorKeys = authorKeys.stream().map(ActorKey::fromKeyString).toList();
        Set<ActorKey> uniqueKeys = Set.copyOf(actorKeys);
        return authorService.getAuthors(uniqueKeys);
    }

    /**
     * Gets a map of the desired authors' IDs to their data.
     * @param authorKeys the keys of the authors to retrieve
     * @return a map of the {@code authorKey}s to their respective {@code author}s
     *
     * @see ActorKey
     */
    @GetMapping("/map")
    public Map<ActorKey, Actor> getAuthorsMap(
        @RequestParam @Size(min = 1) List<String> authorKeys
    ) {
        List<? extends Actor> authors = getAuthorsStream(authorKeys).collectList().block();
        Map<ActorKey, Actor> authorMap = new HashMap<>();
        for (Actor author : authors) {
            authorMap.put(author.getKey(), author);
        }
        return authorMap;
    }
}
