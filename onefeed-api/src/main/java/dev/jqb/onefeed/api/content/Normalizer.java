package dev.jqb.onefeed.api.content;

/**
 * A means of normalizing {@link RawContent} post-retrieval
 * @param <In> the type of {@link RawContent} to normalize
 * @param <Out> the type of {@link NormalizedContent} to produce
 */
public interface Normalizer<In extends RawContent, Out extends NormalizedContent> {

    /**
     * Normalizes the given {@link In}.
     * @param content the piece of {@link In} to normalize
     * @return the {@code content}, normalized as {@link Out}
     */
    Out normalize(In content);
}
