package http.response;

import http.enums.HttpHeader;
import http.enums.HttpStatus;
import http.enums.MimeType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

// HTTP 응답 메시지를 OutputStream 에 직접 쓰는 책임을 가진 클래스.
// forward(path) 는 정적 리소스 200 응답, redirect(path) 는 302 응답을 생성한다.
public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    private static final String WEBAPP_ROOT = "./webapp";
    private static final String CRLF = "\r\n";

    private final DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    // 지정된 path 의 정적 리소스를 200 OK 로 응답한다.
    // 확장자에 맞는 Content-Type 을 자동 설정한다.
    public void forward(String path) {
        try {
            // 1) webapp 디렉토리 기준으로 파일 내용을 byte 로 읽어온다
            byte[] body = Files.readAllBytes(Paths.get(WEBAPP_ROOT + path));
            String contentType = MimeType.getContentType(path);

            // 2) start-line + headers + blank line + body 순으로 작성
            dos.writeBytes("HTTP/1.1 " + HttpStatus.OK.getCode() + " " + HttpStatus.OK.getMessage() + " " + CRLF);
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getKey() + ": " + contentType + CRLF);
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getKey() + ": " + body.length + CRLF);
            dos.writeBytes(CRLF);
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    // 302 Found 응답을 생성한다. Location 헤더만 포함하며 body 는 비어 있다.
    public void redirect(String path) {
        redirect(path, null);
    }

    // 302 + 추가 헤더(예: Set-Cookie) 와 함께 redirect 한다.
    public void redirect(String path, String extraHeaderLine) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND.getCode() + " " + HttpStatus.FOUND.getMessage() + " " + CRLF);
            dos.writeBytes(HttpHeader.LOCATION.getKey() + ": " + path + CRLF);
            if (extraHeaderLine != null && !extraHeaderLine.isEmpty()) {
                dos.writeBytes(extraHeaderLine + CRLF);
            }
            dos.writeBytes(CRLF);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
