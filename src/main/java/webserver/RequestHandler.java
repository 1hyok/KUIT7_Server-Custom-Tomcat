package webserver;

import db.MemoryUserRepository;
import http.enums.HttpHeader;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.routing.Route;
import model.User;

import java.io.*;
import java.net.Socket;
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
            HttpResponse httpResponse = new HttpResponse(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            if (httpRequest == null) return;

            String url = httpRequest.getUrl();
            log.log(Level.INFO, () -> String.format("Request: %s %s", httpRequest.getMethod(), url));

            if (url.equals(Route.SIGNUP.getPath())) {
                handleSignup(httpRequest, httpResponse);
            } else if (url.equals(Route.LOGIN.getPath())) {
                handleLogin(httpRequest, httpResponse);
            } else if (url.equals(Route.USER_LIST.getPath())) {
                handleLoginList(httpRequest, httpResponse);
            } else {
                handleStaticFile(url, httpResponse);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void handleLoginList(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.getCookie().contains("logined=true")) {
            httpResponse.forward("/user/list.html");
            return;
        }
        httpResponse.redirect(Route.LOGIN_PAGE.getPath());
    }

    private void handleLogin(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = httpRequest.getBody();
        log.log(Level.INFO, () -> String.format("Body: %s", body));

        String[] params = body.split("&");
        String[] userIdParam = params[0].split("=");
        if (userIdParam.length < 2) {
            httpResponse.redirect(Route.LOGIN_FAILED.getPath());
            return;
        }
        String userId = userIdParam[1];

        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        boolean isSignedUp = memoryUserRepository.isSignedUp(userId);
        log.log(Level.INFO, () -> String.format("isSignedUp: %s", isSignedUp));
        if (isSignedUp) {
            httpResponse.redirect(INDEX, HttpHeader.SET_COOKIE.getKey() + ": logined=true");
            return;
        }
        httpResponse.redirect(Route.LOGIN_FAILED.getPath());
    }

    private void handleStaticFile(String url, HttpResponse httpResponse) {
        if (url.equals("/")) {
            url = INDEX;
        }
        httpResponse.forward(url);
    }

    private void handleSignup(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = httpRequest.getBody();
        log.log(Level.INFO, () -> String.format("Body: %s", body));

        User user = getUser(body);
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        memoryUserRepository.addUser(user);

        httpResponse.redirect(INDEX);
    }

    private static User getUser(String query) {
        String[] params = query.split("&");
        String userId = params[0].split("=")[1];
        String password = params[1].split("=")[1];
        String name = params[2].split("=")[1];
        String email = params[3].split("=")[1];

        return new User(userId, password, name, email);
    }

}
