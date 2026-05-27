package http.enums;

// URL 의 확장자별 Content-Type 매핑.
// CSS / JS / 이미지 등 정적 리소스 요청을 올바른 MIME 타입으로 응답하기 위해 사용한다.
public enum MimeType {
    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css"),
    JS(".js", "application/javascript"),
    PNG(".png", "image/png"),
    JPEG(".jpeg", "image/jpeg");

    private final String extension;
    private final String contentType;

    MimeType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    // 확장자에 매칭되는 Content-Type 을 반환. 매칭 없으면 HTML 로 fallback.
    public static String getContentType(String url) {
        for (MimeType mimeType : values()) {
            if (url.endsWith(mimeType.extension)) {
                return mimeType.contentType;
            }
        }
        return HTML.contentType;
    }
}
