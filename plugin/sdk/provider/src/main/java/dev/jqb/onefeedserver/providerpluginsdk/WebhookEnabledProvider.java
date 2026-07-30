package dev.jqb.onefeedserver.providerpluginsdk;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.feed.FeedUpdate;
import dev.jqb.onefeed.core.provider.Provider;

/**
 * A {@link Provider} capable of notifying OneFeed when a feed has been updated via webhooks
 *
 * @param <C> the type of {@link Content} produced
 * @param <A> the type of {@link Actor} produced
 */
public abstract class WebhookEnabledProvider<C extends Content, A extends Actor>
    extends Provider<C, A>
{

    /**
     * Constructs a new {@code Provider} with the given ID
     *
     * @param id the unique identifier of this provider
     */
    public WebhookEnabledProvider(String id) {
        super(id);
    }

    /**
     * Gets the updated content that the webhook notification is making us aware of
     *
     * @param notifPayload the payload of the request directed to the webhook path ending with
     *                     a slug matching the ID of the plugin
     *
     * @return the updated content that the webhook notification was referring to
     */
    public abstract FeedUpdate<C, A> handleWebhookNotif(String notifPayload);
}
