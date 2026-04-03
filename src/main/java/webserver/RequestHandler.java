package webserver;

import db.MemoryUserRepository;
import http.enums.HttpHeader;
import http.enums.HttpStatus;
import http.enums.MimeType;
import http.request.HttpRequest;
import http.routing.Route;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final String INDEX = "/index.html";

    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, () -> String.format("New Client Connect! Connected IP : %s, Port : %s", connection.getInetAddress(), connection.getPort()));

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            if (httpRequest == null) return;

            String url = httpRequest.getUrl();
            log.log(Level.INFO, () -> String.format("Request: %s %s", httpRequest.getMethod(), url));

            if (url.equals(Route.SIGNUP.getPath())) {
                handleSignup(httpRequest, dos);
            } else if (url.equals(Route.LOGIN.getPath())) {
                handleLogin(httpRequest, dos);
            } else if (url.equals(Route.USER_LIST.getPath())) {
                handleLoginList(httpRequest, dos);
            } else {
                handleStaticFile(url, dos);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void handleLoginList(HttpRequest httpRequest, DataOutputStream dos) throws IOException {
        if (httpRequest.getCookie().contains("logined=true")) {
            byte[] body = Files.readAllBytes(Paths.get("./webapp/user/list.html"));
            response200Header(dos, body.length, MimeType.getContentType(".html"));
            responseBody(dos, body);
            return;
        }
        response302Header(dos, Route.LOGIN_PAGE.getPath(), null);
    }

    private void handleLogin(HttpRequest httpRequest, DataOutputStream dos) {
        String body = httpRequest.getBody();
        log.log(Level.INFO, () -> String.format("Body: %s", body));

        String[] params = body.split("&");
        String[] userIdParam = params[0].split("=");
        if (userIdParam.length < 2) {
            response302Header(dos, Route.LOGIN_FAILED.getPath(), null);
            return;
        }
        String userId = userIdParam[1];

        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        boolean isSignedUp = memoryUserRepository.isSignedUp(userId);
        log.log(Level.INFO, () -> String.format("isSignedUp: %s", isSignedUp));
        if (isSignedUp) {
            response302Header(dos, INDEX, HttpHeader.SET_COOKIE.getKey() + ": logined=true");
            return;
        }
        response302Header(dos, Route.LOGIN_FAILED.getPath(), null);
    }

    private void handleStaticFile(String url, DataOutputStream dos) throws IOException {
        if (url.equals("/")) {
            url = INDEX;
        }
        byte[] body = Files.readAllBytes(Paths.get("./webapp" + url));

        response200Header(dos, body.length, MimeType.getContentType(url));
        responseBody(dos, body);
    }

    private void handleSignup(HttpRequest httpRequest, DataOutputStream dos) {
        String body = httpRequest.getBody();
        log.log(Level.INFO, () -> String.format("Body: %s", body));

        User user = getUser(body);
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        memoryUserRepository.addUser(user);

        response302Header(dos, INDEX, null);
    }

    private static User getUser(String query) {
        String[] params = query.split("&");
        String userId = params[0].split("=")[1];
        String password = params[1].split("=")[1];
        String name = params[2].split("=")[1];
        String email = params[3].split("=")[1];

        return new User(userId, password, name, email);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            HttpStatus httpStatus = HttpStatus.OK;
            dos.writeBytes("HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.getMessage() + " \r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getKey() + ": " + contentType + "\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getKey() + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path, String cookie) {
        try {
            HttpStatus httpStatus = HttpStatus.FOUND;
            dos.writeBytes("HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.getMessage() + " \r\n");
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
