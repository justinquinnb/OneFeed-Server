package dev.jqb.onefeed.api.content;

/**
 * A means of normalizing {@link PlatformContent} post-retrieval
 * @param <In> the type of {@link PlatformContent} to normalize
 * @param <Out> the type of {@link NormalizedContent} to produce
 */
public interface ContentNormalizer<In extends PlatformContent, Out extends NormalizedContent> {

    /**
     * Normalizes the given {@link In}.
     * @param content the piece of {@link In} to normalize
     * @return the {@code content}, normalized as {@link Out}
     */
    Out normalize(In content);
}
