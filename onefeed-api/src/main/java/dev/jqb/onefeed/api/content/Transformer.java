package dev.jqb.onefeed.api.content;

import org.pf4j.ExtensionPoint;

/**
 * A means of transforming {@link NormalizedContent} into another type, or simply
 * manipulating it
 * @param <In> the type of {@link NormalizedContent} to transform
 * @param <Out> the type of {@link NormalizedContent} to produce
 */
public interface Transformer <In extends NormalizedContent, Out extends NormalizedContent> extends
    ExtensionPoint
{

    /**
     * Transforms the given {@link In} into {@link Out}
     * @param content the piece of {@link In} to transform
     * @return the provided {@code content}, transformed into {@link Out}
     */
    Out transform(In content);
}
