package http.request;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    private static final String TEST_DIRECTORY = "src/test/resources/";

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    void GET_요청_파싱() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(TEST_DIRECTORY + "http_get_request.txt"));

        assertEquals("GET", httpRequest.getMethod());
        assertEquals("/index.html", httpRequest.getUrl());
        assertEquals("HTTP/1.1", httpRequest.getVersion());
        assertEquals("", httpRequest.getBody());
    }

    @Test
    void POST_요청_파싱() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(TEST_DIRECTORY + "http_post_request.txt"));

        assertEquals("POST", httpRequest.getMethod());
        assertEquals("/user/signup", httpRequest.getUrl());
        assertEquals("HTTP/1.1", httpRequest.getVersion());
        assertEquals("48", httpRequest.getHeader("Content-Length"));
        assertEquals("userId=jw&password=password&name=1hyok&email=a@a", httpRequest.getBody());
    }

    @Test
    void Cookie_파싱() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(TEST_DIRECTORY + "http_cookie_request.txt"));

        assertEquals("GET", httpRequest.getMethod());
        assertEquals("/user/userList", httpRequest.getUrl());
        assertTrue(httpRequest.getCookie().contains("logined=true"));
    }
}
