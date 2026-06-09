package dev.jqb.onefeed.app.exception;

import dev.jqb.onefeed.api.feed.MalformedFeedIdException;
import dev.jqb.onefeed.api.feed.UnknownFeedIdException;
import dev.jqb.onefeed.app.aggregation.MalformedAggregateCursorException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MalformedFeedIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetails handleMalformedFeedIdException(MalformedFeedIdException e,
        HttpServletRequest req
    ) {
        return new ProblemDetails(
            "https://github.com/justinquinnb/OneFeed/wiki",
            "Malformed Feed ID",
            400,
            e.getMessage(),
            null
        );
    }

    @ExceptionHandler(UnknownFeedIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetails handleUnknownFeedIdException(UnknownFeedIdException e) {
        return new ProblemDetails(
            "https://github.com/justinquinnb/OneFeed/wiki",
            "Unknown Feed ID",
            400,
            e.getMessage(),
            null
        );
    }

    @ExceptionHandler(MalformedAggregateCursorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetails handleMalformedAggregateCursorException(
        MalformedAggregateCursorException e
    ) {
        return new ProblemDetails(
            "https://github.com/justinquinnb/OneFeed/wiki",
            "Malformed Aggregate Cursor",
            400,
            e.getMessage(),
            null
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetails handleException(Exception e) {
        return new ProblemDetails(
            "https://github.com/justinquinnb/OneFeed/wiki",
            "Internal Server Error",
            500,
            "An unexpected error occurred.",
            null
        );
    }
}
