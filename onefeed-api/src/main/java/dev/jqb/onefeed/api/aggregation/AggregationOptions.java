package dev.jqb.onefeed.api.aggregation;

import dev.jqb.onefeed.api.content.ContentFilter;
import java.util.List;

/**
 * Optional controls over what a final aggregation should contain
 */
public class AggregationOptions {
    public List<ContentFilter<?>> filters;
    public boolean dedupe;
}
