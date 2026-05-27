package http.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// HTTP 요청과 관련된 헬퍼 모음.
public class HttpRequestUtils {
    // form-urlencoded 형식 ("key1=value1&key2=value2") 을 Map 으로 파싱.
    // 형식이 깨졌을 경우(=가 없거나 등) 빈 Map 을 반환하여 호출측을 보호한다.
    public static Map<String, String> parseQueryParameter(String queryString) {
        try {
            String[] queryStrings = queryString.split("&");

            return Arrays.stream(queryStrings)
                    .map(q -> q.split("="))
                    .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}