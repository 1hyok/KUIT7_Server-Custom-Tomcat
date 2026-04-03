package webserver;

import db.MemoryUserRepository;
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
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

//            byte[] body = "Hello World".getBytes();
            String requestLine = br.readLine();
            String url = requestLine.split(" ")[1];
            String finalUrl = url;

            log.log(Level.INFO, () -> String.format("Request: %s", finalUrl));
            if (url.startsWith("user/signup")) {
                String queryString = url.split("\\?")[1];

                String[] params = queryString.split("&");

                String userId = params[0].split("=")[1];
                String password = params[1].split("=")[1];
                String name = params[2].split("=")[1];
                String email = params[3].split("=")[1];

                User user = new User(userId, password, name, email);
                MemoryUserRepository.getInstance().addUser(user);
                log.log(Level.INFO, () -> String.format("회원가임 완료: %s", userId));

                byte[] body = Files.readAllBytes(Paths.get("./webapp" + url));
                response200Header(dos, body.length, "text/html;charset=utf-8");
                responseBody(dos, body);
            } else {
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
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}