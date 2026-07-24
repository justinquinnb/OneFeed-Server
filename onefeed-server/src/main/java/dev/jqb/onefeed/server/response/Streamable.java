package dev.jqb.onefeed.server.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An item that can be streamed
 */
public interface Streamable {

    /**
     * Gets the name to display as the object's type, ideally its class name.
     * @return the name to display as the object's type
     */
    @JsonIgnore
    String getDisplayType();
}
