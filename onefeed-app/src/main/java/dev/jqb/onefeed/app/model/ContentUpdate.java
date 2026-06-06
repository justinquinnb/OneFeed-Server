package dev.jqb.onefeed.app.model;

import dev.jqb.onefeed.api.content.NormalizedContent;

/**
 * A new piece of content sent to a client of OneFeed
 * @param content the new content
 * @param <T> the type of {@link NormalizedContent}
 */
public record ContentUpdate<T extends NormalizedContent>(T content) implements StreamData {}
