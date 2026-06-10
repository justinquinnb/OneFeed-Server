package dev.jqb.onefeed.api.author;

/**
 * A means of transforming {@link NormalizedAuthor} into another type, or simply
 * manipulating it
 * @param <In> the type of {@link NormalizedAuthor} to transform
 * @param <Out> the type of {@link NormalizedAuthor} to produce
 */
public interface AuthorTransformer<In extends NormalizedAuthor, Out extends NormalizedAuthor> {

    /**
     * Transforms the given {@link In} into {@link Out}
     * @param author the normalized author to transform
     * @return the provided {@code author}, transformed into {@link Out}
     */
    Out transform(In author);
}
