package dev.jqb.onefeed.app.exception;


/**
 * A problem details object as defined by RFC 7807.
 *
 * @param type a URI reference to the problem type's documentation
 * @param title a short, human-readable summary of the problem type
 * @param status the HTTP status code
 * @param detail a human-readable explanation specific to this occurrence of the problem
 * @param instance a URI reference that identifies the specific occurrence of the problem
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7807">RFC 7807</a>
 */
public record ProblemDetails(String type, String title, int status, String detail, String instance) {

}
