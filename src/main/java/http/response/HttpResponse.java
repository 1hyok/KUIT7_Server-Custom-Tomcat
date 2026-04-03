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

public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    private final DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String path) {
        try {
            byte[] body = Files.readAllBytes(Paths.get("./webapp" + path));
            String contentType = MimeType.getContentType(path);

            dos.writeBytes("HTTP/1.1 " + HttpStatus.OK.getCode() + " " + HttpStatus.OK.getMessage() + " \r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getKey() + ": " + contentType + "\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getKey() + ": " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void redirect(String path) {
        redirect(path, null);
    }

    public void redirect(String path, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND.getCode() + " " + HttpStatus.FOUND.getMessage() + " \r\n");
            dos.writeBytes(HttpHeader.LOCATION.getKey() + ": " + path + "\r\n");
            if (cookie != null && !cookie.isEmpty()) {
                dos.writeBytes(cookie + "\r\n");
            }
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
