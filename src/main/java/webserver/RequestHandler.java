package webserver;

import db.MemoryUserRepository;
import http.enums.HttpHeader;
import http.enums.HttpStatus;
import http.enums.MimeType;
import http.util.IOUtils;
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

            String requestLine = br.readLine();
            if (requestLine == null) return;
            log.log(Level.INFO, () -> String.format("Request: %s", requestLine));

            String url = requestLine.split(" ")[1];

            switch (url) {
                case "/user/signup" -> {
                    log.log(Level.INFO, "handleSignup");
                    handleSignup(br, dos);
                }
                case "/user/login" -> {
                    log.log(Level.INFO, "handleLogin");
                    handleLogin(br, dos);
                }
                case "/user/userList" -> handleLoginList(br, dos);
                default -> handleStaticFile(url, dos);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void handleLoginList(BufferedReader br, DataOutputStream dos) throws IOException {
        boolean logined = false;
        String line = "";
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            if (line.contains(HttpHeader.COOKIE.getKey() + ": logined=true")) {
                logined = true;
                break;
            }
        }
        if (logined) {
            byte[] body = Files.readAllBytes(Paths.get("./webapp/user/list.html"));
            response200Header(dos, body.length, MimeType.getContentType(".html"));
            responseBody(dos, body);
            return;
        }
        response302Header(dos, "/user/login.html", null);
    }

    private void handleLogin(BufferedReader br, DataOutputStream dos) throws IOException {

        String body = getRequestBody(br);

        String[] params = body.split("&");
        String[] userIdParam = params[0].split("=");
        if (userIdParam.length < 2) {
            response302Header(dos, "/user/login_failed.html", null);
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
        response302Header(dos, "/user/login_failed.html", null);
    }

    private void handleStaticFile(String url, DataOutputStream dos) throws IOException {
        if (url.equals("/")) {
            url = INDEX;
        }
        byte[] body = Files.readAllBytes(Paths.get("./webapp" + url));

        response200Header(dos, body.length, MimeType.getContentType(url));
        responseBody(dos, body);
    }

    private void handleSignup(BufferedReader br, DataOutputStream dos) throws IOException {
//        // 요구사항2
//        String queryString = url.split("\\?")[1];
//
//        User user = getUser(queryString);
//        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
//        memoryUserRepository.addUser(user);
//        log.log(Level.INFO, () -> String.format("회원가입 완료: %s", userId));
//
//        response302Header(dos, INDEX);

        // 요구사항 3
        String body = getRequestBody(br);

        User user = getUser(body);
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        memoryUserRepository.addUser(user);

        response302Header(dos, INDEX, null);
    }

    private static String getRequestBody(BufferedReader br) throws IOException {
        int contentLength = getContentLength(br);
        String body = IOUtils.readData(br, contentLength);
        log.log(Level.INFO, () -> String.format("Body: %s", body));
        return body;
    }

    private static User getUser(String query) {
        String[] params = query.split("&");
        String userId = params[0].split("=")[1];
        String password = params[1].split("=")[1];
        String name = params[2].split("=")[1];
        String email = params[3].split("=")[1];

        return new User(userId, password, name, email);
    }

    private static int getContentLength(BufferedReader br) throws IOException {
        int contentLength = 0;
        while (true) {
            String line = br.readLine();
            if (line.isEmpty()) {
                break;
            }
            if (line.startsWith(HttpHeader.CONTENT_LENGTH.getKey())) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }
        return contentLength;
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