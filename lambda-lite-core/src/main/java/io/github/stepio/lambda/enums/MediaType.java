package io.github.stepio.lambda.enums;

/**
 * Represents common values of Media Type (MIME Type), similar to {@code org.springframework.http.MediaType}.
 */
public enum MediaType {

    ALL("*/*"),
    APPLICATION_ATOM_XML("application/atom+xml"),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
    APPLICATION_JSON("application/json"),
    APPLICATION_JSON_UTF8("application/json;charset=UTF-8"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_PDF("application/pdf"),
    APPLICATION_RSS_XML("application/rss+xml"),
    APPLICATION_XHTML_XML("application/xhtml+xml"),
    APPLICATION_XML("application/xml"),
    IMAGE_GIF("image/gif"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    TEXT_EVENT_STREAM("text/event-stream"),
    TEXT_HTML("text/html"),
    TEXT_MARKDOWN("text/markdown"),
    TEXT_PLAIN("text/plain"),
    TEXT_XML("text/xml");

    private final String value;

    MediaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
