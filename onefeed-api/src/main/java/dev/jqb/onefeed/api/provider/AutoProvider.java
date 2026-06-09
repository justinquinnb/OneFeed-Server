package dev.jqb.onefeed.api.provider;

import dev.jqb.onefeed.api.content.PlatformContent;
import dev.jqb.onefeed.api.feed.FeedUpdate;

/**
 * Denotes a {@link Provider} capable of notifying OneFeed when a feed has been updated via webhooks
 *
 * @param <Out> the type of {@link PlatformContent} produced
 */
public interface AutoProvider<Out extends PlatformContent> extends Provider<Out> {

    /**
     * Gets the updated content that the webhook notification is making us aware of
     *
     * @param notifPayload the payload of the request directed to the webhook path ending with
     *                     a slug matching the ID of the plugin
     *
     * @return the updated content that the webhook notification was referring to
     */
    FeedUpdate<Out> handleWebhookNotif(String notifPayload);
}
