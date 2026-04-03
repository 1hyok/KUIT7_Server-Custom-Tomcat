package http.enums;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie");

    private final String key;

    HttpHeader(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
