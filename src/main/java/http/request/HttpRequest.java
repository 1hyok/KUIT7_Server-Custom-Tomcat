package http.request;

import http.enums.HttpHeader;
import http.enums.HttpMethod;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// HTTP 요청 메시지(start line + headers + body) 를 파싱한 결과를 담는 불변 객체.
// 정적 팩토리 메서드 from(BufferedReader) 로만 인스턴스를 생성한다.
public class HttpRequest {
    private final HttpMethod method;
    private final String url;
    private final String version;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(HttpMethod method, String url, String version, Map<String, String> headers, String body) {
        this.method = method;
        this.url = url;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        // 1) Start Line 파싱: "GET /index.html HTTP/1.1"
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            // 빈 요청은 파싱 불가 — 호출측에서 처리할 수 있도록 null 반환
            return null;
        }

        String[] startLine = requestLine.split(" ");
        HttpMethod method = HttpMethod.from(startLine[0]);
        String url = startLine[1];
        String version = startLine[2];

        // 2) Headers 파싱: 빈 줄(\r\n) 을 만날 때까지 "Key: Value" 형식으로 읽어 들임
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ", 2);
            headers.put(headerParts[0], headerParts[1]);
        }

        // 3) Body 파싱: Content-Length 헤더가 있을 때만 그 길이만큼 읽는다.
        //    (BufferedReader.readLine 은 body 의 \r\n 부재로 무한 대기할 수 있어 길이 기반 읽기 필수)
        String body = "";
        String contentLengthKey = HttpHeader.CONTENT_LENGTH.getKey();
        if (headers.containsKey(contentLengthKey)) {
            int contentLength = Integer.parseInt(headers.get(contentLengthKey));
            body = IOUtils.readData(br, contentLength);
        }

        return new HttpRequest(method, url, version, headers, body);
    }

    public HttpMethod getMethod() {
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

    // Cookie 헤더가 없을 때는 빈 문자열을 반환하여 호출측 NPE 를 방지한다.
    public String getCookie() {
        return headers.getOrDefault(HttpHeader.COOKIE.getKey(), "");
    }

    public String getBody() {
        return body;
    }

    // 외부에서 헤더를 임의 수정하지 못하도록 unmodifiable view 제공
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
