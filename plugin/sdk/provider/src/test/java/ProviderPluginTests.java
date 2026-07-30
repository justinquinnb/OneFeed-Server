import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.actor.ActorTransformer;
import dev.jqb.onefeed.core.actor.OneFeedActor;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeed.core.content.ContentTransformer;
import dev.jqb.onefeed.core.content.OneFeedAttachment;
import dev.jqb.onefeed.core.content.OneFeedContent;
import dev.jqb.onefeed.core.content.OneFeedMedia;
import dev.jqb.onefeed.core.feed.Feed;
import dev.jqb.onefeed.core.feed.FeedAttribution;
import dev.jqb.onefeed.core.feed.FeedId;
import dev.jqb.onefeed.core.platform.ExternalRef;
import dev.jqb.onefeed.core.platform.Platform;
import dev.jqb.onefeed.core.provider.Provider;
import dev.jqb.onefeedserver.pluginsdk.test.OneFeedPluginTests;
import dev.jqb.onefeedserver.providerpluginsdk.ProviderPlugin;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Flux;

/**
 * Tests the basic functionalities of a {@link ProviderPlugin}, particularly its provided
 * {@link Provider} implementation
 *
 * @param <T> your specific {@link ProviderPlugin} implementation
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class ProviderPluginTests<T extends ProviderPlugin>
    extends OneFeedPluginTests<T> {

    public Provider<Content, Actor> provider;
    public int contentPerPageLimit;
    public Set<String> authorIds = new HashSet<>();
    public Content contentNormalizerInput;
    public OneFeedContent expectedContentNormalizerOutput;
    public Actor authorNormalizerInput;
    public OneFeedActor expectedAuthorNormalizerOutput;

    @BeforeAll
    public void getProvider() {
        this.provider = (Provider<Content, Actor>) plugin.getProvider();
        this.contentPerPageLimit = getContentPerPageLimit();
        this.contentNormalizerInput = getContentNormalizerInput();
        this.expectedContentNormalizerOutput = getExpectedContentNormalizerOutput();
        this.authorNormalizerInput = getAuthorNormalizerInput();
        this.expectedAuthorNormalizerOutput = getExpectedAuthorNormalizerOutput();
    }

    /**
     * Get the maximum number of content pieces that the provider will return per page.
     *
     * @return the maximum number of content pieces that the provider will return per page
     */
    protected abstract int getContentPerPageLimit();

    /**
     * Gets a sample piece of content to attempt to normalize.
     *
     * @return a sample piece of content to attempt to normalize
     */
    protected abstract Content getContentNormalizerInput();

    /**
     * Gets the sample piece of content correctly normalized as a piece of {@link OneFeedContent}.
     *
     * @return a sample piece of content correctly normalized as a piece of {@link OneFeedContent}
     */
    protected abstract OneFeedContent getExpectedContentNormalizerOutput();

    /**
     * Gets a sample author to attempt to normalize.
     *
     * @return a sample author to attempt to normalize
     */
    protected abstract Actor getAuthorNormalizerInput();

    /**
     * Gets the sample author correctly normalized as a piece of {@link OneFeedContent}.
     *
     * @return a sample author correctly normalized as a piece of {@link OneFeedContent}
     */
    protected abstract OneFeedActor getExpectedAuthorNormalizerOutput();

    @Test
    public void contentNormalizerWorksAsExpected() {
        ContentTransformer<Content, OneFeedContent> contentNormalizer =
            provider.getContentNormalizer();
        OneFeedContent normalizerOutput = contentNormalizer.transform(contentNormalizerInput);

        validateOfcEquality(normalizerOutput, expectedContentNormalizerOutput);
    }

    @Test
    public void authorNormalizerWorksAsExpected() {
        ActorTransformer<Actor, OneFeedActor> actorNormalizer =
            provider.getActorNormalizer();
        OneFeedActor normalizerOutput = actorNormalizer.transform(authorNormalizerInput);

        validateOfaEquality(normalizerOutput, expectedAuthorNormalizerOutput);
    }

    /**
     * Ensure there are feeds configured for testing
     */
    @Test
    public void feedsAreConfigured() {
        assertNotNull(plugin.getProvider().getFeeds());
    }

    /**
     * The provider returns complete platform info
     */
    @Test
    public void platformInfoIsComplete() {
        Platform platform = provider.getPlatform();
        assertNotNull(platform);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(platform.getName()).isNotEmpty()
            .as("Platform name is specified");
        softly.assertThat(platform.getHomepageUrl()).isNotEmpty()
            .as("Platform homepage URL is specified");
        softly.assertAll();
    }

    /**
     * For every feed specified for testing, try retrieving its profile
     */
    @TestFactory
    @Order(3)
    public Stream<DynamicTest> retrieveContentAuthors() {
        return authorIds.stream().map(authorId ->
            DynamicTest.dynamicTest("Author retrieval for: " + authorId, () -> {
                retrieveContentAuthor(authorId);
            })
        );
    }

    /**
     * Test retrieval of the given author, validating a successful response and the
     * existence of the resulting {@link Actor} and its fields
     *
     * @param authorId the ID of the author on the platform to try retrieving
     */
    public void retrieveContentAuthor(String authorId) {
        Actor author = provider.fetchAuthor(authorId).block();
        log.debug("Retrieved platform author: {}", author);
        assertNotNull(author);
    }

    /**
     * For every feed specified for testing, try retrieving a single piece of content
     */
    @Order(1)
    @TestFactory
    public Stream<DynamicTest> retrieveSingleContentFromFeeds() {
        return provider.getFeeds().stream().map(feed ->
            DynamicTest.dynamicTest("Singular content retrieval for: " + feed.getId().feedName(), () -> {
                retrieveSingleContent(feed);
            })
        );
    }

    /**
     * Test retrieval of the given feed's {@link Content}, validating a successful response and the
     * existence of the basic {@link Content} fields
     *
     * @param feed the feed whose content to try retrieving
     */
    private void retrieveSingleContent(Feed<Content> feed) {
        Flux<Content> flux = feed.fetchRecentContent(1);
        List<Content> contentList = flux.collectList().block();
        assertNotNull(contentList);

        // Not necessarily a fail because the feed may just have no content
        if (contentList.isEmpty()) {
            log.warn("No content retrieved for feed: {}", feed.getId().feedName());
        }

        assert (contentList.size() <= 1);

        Content content = contentList.getFirst();
        log.debug("Retrieved platform content: {}", content);
        authorIds.addAll(content.getAuthorIds());

        validateContent(content);
    }

    /**
     * For every feed specified for testing, try retrieving two pages of content
     */
    @Order(2)
    @TestFactory
    public Stream<DynamicTest> retrieveTwoContentPagesFromFeeds() {
        return provider.getFeeds().stream().map(feed ->
            DynamicTest.dynamicTest("Multi-page content retrieval for: " + feed.getId().feedName(), () -> {
                retrieveTwoContentPages(feed);
            })
        );
    }

    /**
     * Test retrieval of two pages of the given feed's {@link Content}, validating a successful
     * response and the existence of the basic {@link Content} fields
     *
     * @param feed the name of the feed whose content to try retrieving
     */
    private void retrieveTwoContentPages(Feed<Content> feed) {
        Flux<Content> flux = feed.fetchRecentContent(contentPerPageLimit + 1);
        List<Content> contentList = flux.collectList().block();

        assertNotNull(contentList);

        // Not necessarily a fail because the feed may just have no content
        if (contentList.isEmpty()) {
            log.warn("No content retrieved for feed: {}", feed.getId().feedName());
        }

        assert (contentList.size() <= contentPerPageLimit + 1);

        if (contentList.size() < contentPerPageLimit + 1) {
            log.warn("Less than expected content retrieved for: {} ({} expected)",
                contentList.size(), contentPerPageLimit + 1);
        }

        Content content = contentList.getLast();
        log.debug("Testing validity of last content piece: {}", content);
        authorIds.addAll(content.getAuthorIds());

        validateContent(content);
    }

    /**
     * Validates the existence of the basic {@link Content} fields
     *
     * @param content the {@link Content} to validate
     */
    private static void validateContent(Content content) {
        assertNotNull(content);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(content.getPublished()).as("Published is not null")
            .isNotNull();
        softly.assertThat(content.getProviderId()).as("Provider ID is not blank").isNotBlank();
        softly.assertAlso(validateFeedId(content.getFeedId()));
        softly.assertAlso(validateExternalRef(content.getExternalRef()));

        softly.assertAll();
    }

    /**
     * Validates the equality of the given {@link OneFeedContent} pieces.
     *
     * @param actual   the piece of content to validate
     * @param expected the piece of content to compare against
     */
    private static void validateOfcEquality(OneFeedContent actual, OneFeedContent expected) {
        log.debug("Validating each piece's base Content data...");
        validateContent(actual);
        validateContent(expected);

        log.debug("Validating the equality of actual content:\n{}\nagainst expected content:\n{}",
            actual, expected);

        // Base Content info
        // ID and external ref
        SoftAssertions softly = new SoftAssertions();
        softly.assertAlso(validateFeedAttributionEquality(actual.getSource(), expected.getSource()));
        softly.assertAlso(validateExternalRefEquality(actual.getExternalRef(),
            expected.getExternalRef()));

        // All other base Content fields
        softly.assertThat(actual.getPublished()).as("Published dates match")
            .isEqualTo(expected.getPublished());

        softly.assertThat(actual.getNextPageCursor()).as("Next page cursors match")
            .isEqualTo(expected.getNextPageCursor());

        // Authors
        boolean actualHasAuthors = actual.getAuthorIds() != null;
        boolean expectedHasAuthors = expected.getAuthorIds() != null;
        softly.assertThat(actualHasAuthors).as("Author existence matches").isEqualTo(expectedHasAuthors);
        if (actualHasAuthors && expectedHasAuthors) {
            softly.assertThat(actual.getAuthorIds().size()).as("Author count matches")
                .isEqualTo(expected.getAuthorIds().size());

            log.debug("Validating the equality of each author...");
            for (int i = 0; i < actual.getAuthorIds().size(); i++) {
                softly.assertThat(actual.getAuthorIds().get(i))
                    .as("Author ID " + i + " matches")
                    .isEqualTo(expected.getAuthorIds().get(i));
            }
        }

        // Primary reaction count
        softly.assertThat(actual.getPrimaryReactionCount())
            .as("Primary reaction counts match")
            .isEqualTo(expected.getPrimaryReactionCount());

        // Textual content
        softly.assertThat(actual.getTitle()).as("Titles match")
            .isEqualTo(expected.getTitle());

        softly.assertThat(actual.getBody()).as("Bodies match")
            .isEqualTo(expected.getBody());

        // Attachments
        boolean actualHasAttachments = actual.getAttachments() != null;
        boolean expectedHasAttachments = expected.getAttachments() != null;
        softly.assertThat(actualHasAttachments).as("OneFeedAttachment existence matches")
            .isEqualTo(expectedHasAttachments);

        if (actualHasAttachments && expectedHasAttachments) {
            softly.assertThat(actual.getAttachments().size()).as("OneFeedAttachment count matches")
                .isEqualTo(expected.getAttachments().size());

            log.debug("Validating each piece's attachment data...");
            for (int i = 0; i < actual.getAttachments().size(); i++) {
                softly.assertAlso(
                    validateAttachmentEquality(actual.getAttachments().get(i), expected.getAttachments().get(i), i)
                );
            }
        }

        softly.assertAll();
    }

    /**
     * Validates the equality of the given {@link OneFeedActor}s.
     *
     * @param actual the author to validate
     * @param expected the author to compare against
     */
    private static void validateOfaEquality(OneFeedActor actual, OneFeedActor expected) {
        log.debug("Validating the equality of actual author:\n{}\nagainst expected author:\n{}",
            actual, expected);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actual.getProviderId()).as("Provider IDs match")
            .isEqualTo(expected.getProviderId());
        softly.assertAlso(validateExternalRefEquality(actual.getExternalRef(), expected.getExternalRef()));
        
        // handle, name, profile pic src
        softly.assertThat(actual.getHandle()).as("Handles match")
            .isEqualTo(expected.getHandle());
        softly.assertThat(actual.getName()).as("Names match")
            .isEqualTo(expected.getName());
        softly.assertThat(actual.getProfilePicSrc()).as("Profile pic srcs match")
            .isEqualTo(expected.getProfilePicSrc());

        softly.assertAll();
    }

    /**
     * Validates the equality of the given {@link OneFeedAttachment} pieces.
     *
     * @param actual   the piece of media to validate
     * @param expected the piece of media to compare against
     * @return a {@link SoftAssertions} object containing the results of the validation
     */
    private static SoftAssertions validateAttachmentEquality(
        OneFeedAttachment actual, OneFeedAttachment expected, int mediaNum) {
        log.debug("Validating the equality of actual media:\n{}\nagainst expected media:\n{}",
            actual, expected);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(actual.getHref())
            .as("OneFeedAttachment %s's hrefs match", mediaNum)
            .isEqualTo(expected.getHref());

        softly.assertThat(actual.getThumbnailSrc())
            .as("OneFeedAttachment %s's thumbnail srcs match", mediaNum)
            .isEqualTo(expected.getThumbnailSrc());

        softly.assertThat(actual.getTitle())
            .as("OneFeedAttachment %s's titles match", mediaNum)
            .isEqualTo(expected.getTitle());

        softly.assertThat(actual.getCaption())
            .as("OneFeedAttachment %s's captions match", mediaNum)
            .isEqualTo(expected.getCaption());

        if ((actual instanceof OneFeedMedia) && (expected instanceof OneFeedMedia)) {
            OneFeedMedia actualMedia = (OneFeedMedia) actual;
            OneFeedMedia expectedMedia = (OneFeedMedia) expected;

            softly.assertThat(actualMedia.getMimeType())
                .as("OneFeedAttachment %s's MIME types match", mediaNum)
                .isEqualTo(expectedMedia.getMimeType());

            softly.assertThat(actualMedia.getSrc())
                .as("OneFeedAttachment %s's srcs match", mediaNum)
                .isEqualTo(expectedMedia.getSrc());

            softly.assertThat(actualMedia.getAltText())
                .as("OneFeedAttachment %s's alt texts match", mediaNum)
                .isEqualTo(expectedMedia.getAltText());
        }

        softly.assertThat(actual.getClass())
            .as("OneFeedAttachment %s's classes match", mediaNum)
                .isEqualTo(expected.getClass());

        return softly;
    }

    /**
     * Validates the existence of the basic {@link Actor} fields
     *
     * @param author the {@link Actor to validate
     * @return a {@link SoftAssertions} object containing the results of the validation
     */
    private static SoftAssertions validateBaseAuthorFields(Actor author) {
        assertNotNull(author);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(author.getProviderId())
            .as("Provider ID is not blank").isNotBlank();
        softly.assertAlso(validateExternalRef(author.getExternalRef()));
        softly.assertThat(author.getHandle()).as("Handle is not blank")
            .isNotBlank();

        return softly;
    }

    /**
     * Validates the existence of the basic {@link FeedId} fields
     * @param feedId the feed ID object to validate
     * @return a {@link SoftAssertions} object containing the results of the validation
     */
    private static SoftAssertions validateFeedId(FeedId feedId) {
        assertNotNull(feedId);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(feedId.getProviderId())
            .as("Provider ID is not blank").isNotBlank();
        softly.assertThat(feedId.feedName())
            .as("Feed name is not blank").isNotBlank();
        return softly;
    }

    /**
     * Validates the existence of the basic {@link FeedAttribution} fields
     * @param feedAttribution the feed attribution object to validate
     * @return a {@link SoftAssertions} object containing the results of the validation
     */
    private static SoftAssertions validateFeedAttribution(FeedAttribution feedAttribution) {
        assertNotNull(feedAttribution);

        SoftAssertions softly = new SoftAssertions();
        softly.assertAlso(validateFeedId(feedAttribution.feedId()));
        softly.assertThat(feedAttribution.feedUrl()).as("Feed URL is not blank").isNotBlank();
        return softly;
    }

    /**
     * Validates the equality of the given {@link FeedAttribution} objects.
     * @param actual the feed attribution to validate
     * @param expected the feed attribution to compare against
     * @return a {@link SoftAssertions} object containing the results of the validation
     */
    private static SoftAssertions validateFeedAttributionEquality(FeedAttribution actual,
        FeedAttribution expected
    ) {
        log.debug("Validating the equality of actual feed attribution:\n{}\nagainst expected feed attribution:\n{}",
            actual, expected);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actual.feedId()).as("Feed IDs on platform match").isEqualTo(expected.feedId());
        softly.assertThat(actual.feedUrl()).as("Feed URLs on platform match").isEqualTo(expected.feedUrl());
        return softly;
    }

    /**
     * Validates the equality of the given {@link ExternalRef} objects.
     *
     * @param actual the external reference to validate
     * @param expected the external reference to compare against
     *
     * @return a {@link SoftAssertions} object containing the results of the validation
     */
    private static SoftAssertions validateExternalRefEquality(ExternalRef actual,
        ExternalRef expected
    ) {
        log.debug("Validating the equality of actual external ref:\n{}\nagainst expected external ref:\n{}",
            actual, expected);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actual.id()).as("IDs on platform match")
            .isEqualTo(expected.id());
        softly.assertThat(actual.url()).as("URLs on platform match")
            .isEqualTo(expected.url());
        return softly;
    }

    /**
     * Validates the existence of the basic {@link ExternalRef} fields
     * @param externalRef the external reference object to validate
     * @return a {@link SoftAssertions} object containing the results of the validation
     */
    private static SoftAssertions validateExternalRef(ExternalRef externalRef) {
        assertNotNull(externalRef);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(externalRef.id())
            .as("ID on platform is not blank").isNotBlank();
        softly.assertThat(externalRef.url())
            .as("URL on platform is not blank").isNotBlank();
        return softly;
    }
}
