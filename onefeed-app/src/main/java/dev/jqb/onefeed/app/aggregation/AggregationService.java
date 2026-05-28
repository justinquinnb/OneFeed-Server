package dev.jqb.onefeed.app.aggregation;

import dev.jqb.onefeed.api.aggregation.Aggregator;
import dev.jqb.onefeed.api.content.Content;
import dev.jqb.onefeed.api.content.ContentFilter;
import dev.jqb.onefeed.api.content.RawContent;
import dev.jqb.onefeed.api.feed.Feed;
import dev.jqb.onefeed.api.feed.Provider;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Responsible for aggregating content from {@link Provider}s
 */
@Service
public class AggregationService implements Aggregator {

    private final JsonMapper jsonMapper;

    @Autowired
    public AggregationService(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public List<RawContent> aggregate(int amount, List<Feed<?>> feeds,
        List<ContentFilter<?>> filters, HashMap<String, String> feedConfigs) {
        return List.of();
    }

    @Override
    public List<RawContent> aggregate(int amount, List<Feed<?>> feeds,
        String aggregateCursor, List<ContentFilter<?>> filters,
        HashMap<String, String> feedConfigs) {
        return List.of();
    }

    @Override
    public String generateAggregateCursor(List<RawContent> content) {
        // Feed name -> oldest content for that feed
        HashMap<String, Content> oldestFeedContent = new HashMap<>();

        // Feed name -> nextPageCursor string for that feed
        HashMap<String, String> cursorMap = new HashMap<>();

        // For all content in the aggregation list...
        for (RawContent c : content) {
            String feedId = c.getSource().getFeedId().toIdString();

            // Associate the current content piece with the feed if no entry exists already
            Content currentOldest = oldestFeedContent.putIfAbsent(feedId, c);
            cursorMap.putIfAbsent(feedId, c.getNextPageCursor());

            // Else, as is the case when an entry already existed for that feed, replace it if
            // this content is older
            if (c.getPublished().isBefore(currentOldest.getPublished())) {
                oldestFeedContent.put(feedId, c);
                cursorMap.put(feedId, c.getNextPageCursor());
            }
        }

        // Then, serialize and encode it
        byte[] jsonBytes = jsonMapper.writeValueAsBytes(cursorMap);
        return Base64.getEncoder().encodeToString(jsonBytes);
    }

    @Override
    public HashMap<String, String> decodeAggregateCursor(String aggregateCursor) {
        String decoded = new String(Base64.getDecoder().decode(aggregateCursor));
        return jsonMapper.readValue(decoded, new TypeReference<HashMap<String, String>>() {});
    }
}
