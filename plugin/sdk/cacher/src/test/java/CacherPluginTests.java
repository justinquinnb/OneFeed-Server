import static org.junit.jupiter.api.Assertions.assertNull;

import dev.jqb.onefeed.core.actor.Actor;
import dev.jqb.onefeed.core.caching.Cacher;
import dev.jqb.onefeed.core.content.Content;
import dev.jqb.onefeedserver.cacherpluginsdk.CacherPlugin;
import dev.jqb.onefeedserver.pluginsdk.test.OneFeedPluginTests;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Tests the basic functionalities of a {@link CacherPlugin}, particularly its provided
 * {@link Cacher} implementation
 *
 * @param <T> your specific {@link CacherPlugin} implementation
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class CacherPluginTests<T extends CacherPlugin> extends OneFeedPluginTests<T>
{
    public Cacher<Content, Actor> cacher;
    public Content sampleContent;
    public Content updatedSampleContent;
    public Actor sampleAuthor;
    public Actor updatedSampleAuthor;

    @BeforeAll
    public void getCacher() {
        this.cacher = (Cacher<Content, Actor>) plugin.getCacher();
        this.sampleContent = this.getSampleContent();
        this.updatedSampleContent = this.getUpdatedSampleContent();
        this.sampleAuthor = this.getSampleAuthor();
        this.updatedSampleAuthor = this.getUpdatedSampleAuthor();
    }

    protected abstract Content getSampleContent();
    protected abstract Actor getSampleAuthor();
    protected abstract Content getUpdatedSampleContent();
    protected abstract Actor getUpdatedSampleAuthor();
    protected abstract boolean contentMatches(Content one, Content two);
    protected abstract boolean authorsMatch(Actor one, Actor two);

    /**
     * Test whether errors are thrown when caching content.
     */
    @Test
    @Order(1)
    public void cacheSampleContent() {
        cacher.cacheContent(List.of(sampleContent));
    }

    /**
     * Test whether errors are thrown when caching an author.
     */
    @Test
    @Order(2)
    public void cacheSampleAuthor() {
        cacher.cacheAuthor(sampleAuthor);
    }

    /**
     * Test whether the sample content is retrievable from the cache.
     */
    @Test
    @Order(3)
    public void fetchSampleContent() {
        Content content = cacher.fetchContent(sampleContent.getKey());
        assert contentMatches(sampleContent, content);
    }

    /**
     * Test whether the sample author is retrievable from the cache.
     */
    @Test
    @Order(4)
    public void fetchSampleAuthor() {
        Actor author = cacher.fetchAuthor(sampleAuthor.getKey());
        assert authorsMatch(sampleAuthor, author);
    }

    /**
     * Test whether the sample content can be updated in the cache without error
     */
    @Test
    @Order(5)
    public void updateSampleContent() {
        cacher.cacheContent(List.of(updatedSampleContent));
        Content updatedSampleContent = cacher.fetchContent(sampleContent.getKey());
        assert contentMatches(sampleContent, updatedSampleContent);
    }

    @Test
    @Order(6)
    public void updateSampleAuthor() {
        cacher.cacheAuthor(updatedSampleAuthor);
        Actor updatedSampleAuthor = cacher.fetchAuthor(sampleAuthor.getKey());
        assert authorsMatch(sampleAuthor, updatedSampleAuthor);
    }

    @Test
    @Order(7)
    public void removeSampleContent() {
        cacher.removeContent(sampleContent.getKey());
        assertNull(cacher.fetchContent(sampleContent.getKey()));
    }

    @Test
    @Order(8)
    public void removeSampleAuthor() {
        cacher.removeAuthor(sampleAuthor.getKey());
        assertNull(cacher.fetchAuthor(sampleAuthor.getKey()));
    }
}
