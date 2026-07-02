package dev.jqb.onefeed.server.model;

import dev.jqb.onefeed.core.platform.Platform;
import lombok.Getter;
import lombok.Setter;

/**
 * A platform sent to a client of OneFeed
 */
@Getter
@Setter
public final class StreamedPlatform extends StreamData {

    /**
     * The platform information to be streamed
     */
    private Platform platform;

    public StreamedPlatform(Platform platform) {
        this.platform = platform;
    }
}
