package dev.jqb.onefeed.app.aggregation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/aggregation")
public class AggregationController {
    private final AggregationService aggregationService;
    private final FeedRegistry feedRegistry;

    @Autowired
    public AggregationController(AggregationService aggregationService, FeedRegistry feedRegistry) {
        this.aggregationService = aggregationService;
        this.feedRegistry = feedRegistry;
    }


}
