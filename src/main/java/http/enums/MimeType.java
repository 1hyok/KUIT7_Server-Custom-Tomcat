package http.enums;

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

    public static String getContentType(String url) {
        for (MimeType mimeType : values()) {
            if (url.endsWith(mimeType.extension)) {
                return mimeType.contentType;
            }
        }
        return HTML.contentType;
    }
}
