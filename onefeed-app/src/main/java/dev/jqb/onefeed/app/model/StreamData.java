package dev.jqb.onefeed.app.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ContentUpdate.class, name = "CONTENT"),
    @JsonSubTypes.Type(value = AuthorUpdate.class, name = "AUTHOR")
})
public sealed interface StreamData permits ContentUpdate, AuthorUpdate {}
