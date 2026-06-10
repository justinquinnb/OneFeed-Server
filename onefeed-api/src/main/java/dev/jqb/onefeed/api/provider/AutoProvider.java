package dev.jqb.onefeed.api.provider;

import dev.jqb.onefeed.api.author.PlatformAuthor;
import dev.jqb.onefeed.api.content.PlatformContent;
import dev.jqb.onefeed.api.feed.FeedUpdate;

/**
 * Denotes a {@link Provider} capable of notifying OneFeed when a feed has been updated via webhooks
 *
 * @param <C> the type of {@link PlatformContent} produced
 * @param <A> the type of {@link PlatformAuthor} produced
 */
public interface AutoProvider<C extends PlatformContent, A extends PlatformAuthor> extends
    Provider<C, A>
{

    /**
     * Gets the updated content that the webhook notification is making us aware of
     *
     * @param notifPayload the payload of the request directed to the webhook path ending with
     *                     a slug matching the ID of the plugin
     *
     * @return the updated content that the webhook notification was referring to
     */
    FeedUpdate<C, A> handleWebhookNotif(String notifPayload);
}
