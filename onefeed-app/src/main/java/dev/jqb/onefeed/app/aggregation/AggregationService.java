package dev.jqb.onefeed.app.aggregation;

import dev.jqb.onefeed.api.aggregation.Aggregator;
import dev.jqb.onefeed.api.content.ContentFilter;
import dev.jqb.onefeed.api.content.NormalizedContent;
import dev.jqb.onefeed.api.feed.Feed;
import dev.jqb.onefeed.api.feed.Provider;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Responsible for aggregating content from {@link Provider}s
 */
@Service
public class AggregationService implements Aggregator {

    @Override
    public List<NormalizedContent> aggregate(int amount, List<Feed<?>> feeds,
        List<ContentFilter<?>> filters, HashMap<String, String> feedConfigs) {
        return List.of();
    }
}
