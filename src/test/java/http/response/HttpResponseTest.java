package http.response;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpResponseTest {

    @Test
    void forward_HTML파일_반환() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpResponse httpResponse = new HttpResponse(out);

        httpResponse.forward("/index.html");

        String response = out.toString();
        assertTrue(response.contains("200 OK"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("Content-Length:"));
    }

    @Test
    void redirect_302_반환() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpResponse httpResponse = new HttpResponse(out);

        httpResponse.redirect("/index.html");

        String response = out.toString();
        assertTrue(response.contains("302 Found"));
        assertTrue(response.contains("Location: /index.html"));
    }

    @Test
    void redirect_쿠키포함_302_반환() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpResponse httpResponse = new HttpResponse(out);

        httpResponse.redirect("/index.html", "Set-Cookie: logined=true");

        String response = out.toString();
        assertTrue(response.contains("302 Found"));
        assertTrue(response.contains("Location: /index.html"));
        assertTrue(response.contains("Set-Cookie: logined=true"));
    }
}
