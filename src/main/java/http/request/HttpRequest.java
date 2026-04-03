package http.request;

import http.enums.HttpHeader;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String url;
    private String version;
    private Map<String, String> headers;
    private String body;

    private HttpRequest(String method, String url, String version, Map<String, String> headers, String body) {
        this.method = method;
        this.url = url;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        // 1) Start Line 파싱: "GET /index.html HTTP/1.1"
        String requestLine = br.readLine();
        if (requestLine == null) return null;

        String[] startLine = requestLine.split(" ");
        String method = startLine[0];
        String url = startLine[1];
        String version = startLine[2];

        // 2) Headers 파싱
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ", 2);
            headers.put(headerParts[0], headerParts[1]);
        }

        // 3) Body 파싱 (Content-Length가 있을 때만)
        String body = "";
        String contentLength = HttpHeader.CONTENT_LENGTH.getKey();
        if (headers.containsKey(contentLength)) {
            int parsedContentLength = Integer.parseInt(headers.get(contentLength));
            body = IOUtils.readData(br, parsedContentLength);
        }

        return new HttpRequest(method, url, version, headers, body);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getCookie() {
        return headers.getOrDefault(HttpHeader.COOKIE.getKey(), "");
    }

    public String getBody() {
        return body;
    }
}
