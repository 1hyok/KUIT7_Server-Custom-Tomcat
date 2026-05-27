package http.enums;

import java.util.Arrays;

// HTTP 메서드 enum.
// 문자열 비교 대신 enum 비교를 통해 오타 위험을 제거하고 타입 안전성을 확보한다.
public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    HEAD,
    OPTIONS;

    // 요청 라인에서 파싱한 문자열을 enum 으로 변환.
    // 알 수 없는 메서드면 IllegalArgumentException 을 던진다.
    public static HttpMethod from(String name) {
        return Arrays.stream(values())
                .filter(m -> m.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported HTTP method: " + name));
    }

    public boolean isEqual(String name) {
        return this.name().equalsIgnoreCase(name);
    }
}
