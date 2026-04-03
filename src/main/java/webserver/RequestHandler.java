package webserver;

import db.MemoryUserRepository;
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

            String url = requestLine.split(" ")[1];
            log.log(Level.INFO, () -> String.format("Request: %s", url));

            if (url.startsWith("/user/signup")) {
                handleSignup(br, dos);
            } else {
                handleStaticFile(url, dos);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void handleStaticFile(String url, DataOutputStream dos) throws IOException {
        if (url.equals("/")) {
            url = "/index.html";
        }
        byte[] body = Files.readAllBytes(Paths.get("./webapp" + url));


        String contentType = "text/html;charset=utf-8";
        if (url.endsWith(".css")) contentType = "text/css";
        if (url.endsWith(".js")) contentType = "application/javascript";
        if (url.endsWith(".png")) contentType = "image/png";
        if (url.endsWith(".jpeg")) contentType = "image/jpeg";

        response200Header(dos, body.length, contentType);
        responseBody(dos, body);
    }

    private void handleSignup(BufferedReader br, DataOutputStream dos) throws IOException {
        //                 요구사항 2
//                String queryString = url.split("\\?")[1];
//
//                String[] params = queryString.split("&");
//
//                String userId = params[0].split("=")[1];
//                String password = params[1].split("=")[1];
//                String name = params[2].split("=")[1];
//                String email = params[3].split("=")[1];
//
//                User user = new User(userId, password, name, email);
//                MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
//                memoryUserRepository.addUser(user);
//                log.log(Level.INFO, () -> String.format("회원가입 완료: %s", userId));
//
//                response302Header(dos, "/index.html");

        // 요구사항 3
        int contentLength = 0;
        while (true) {
            String line = br.readLine();
            if (line.isEmpty()) {
                break;
            }
            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }
        String body = IOUtils.readData(br, contentLength);
        log.log(Level.INFO, () -> String.format("Body: %s", body));

        String[] params = body.split("&");
        String userId = params[0].split("=")[1];
        String password = params[1].split("=")[1];
        String name = params[2].split("=")[1];
        String email = params[3].split("=")[1];

        User user = new User(userId, password, name, email);
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        memoryUserRepository.addUser(user);

        response302Header(dos, "/index.html");
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
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