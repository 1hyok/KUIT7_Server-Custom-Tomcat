package http.enums;

// HTTP 헤더 이름 상수. 오타 방지 및 일관성 유지를 위해 enum 으로 관리한다.
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
