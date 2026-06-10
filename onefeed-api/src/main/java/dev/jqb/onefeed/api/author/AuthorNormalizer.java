package dev.jqb.onefeed.api.author;

/**
 * A means of normalizing {@link PlatformAuthor}s post-retrieval
 * @param <In> the type of {@link PlatformAuthor} to normalize
 * @param <Out> the type of {@link NormalizedAuthor} to produce
 */
public interface AuthorNormalizer<In extends PlatformAuthor, Out extends NormalizedAuthor>{
    /**
     * Normalizes the given {@link In}.
     * @param author the piece of {@link In} to normalize
     * @return the {@code author}, normalized as {@link Out}
     */
    Out normalize(In author);
}
