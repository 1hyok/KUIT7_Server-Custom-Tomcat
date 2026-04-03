package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

public class SignUpController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = httpRequest.getBody();

        String[] params = body.split("&");
        String userId = params[0].split("=")[1];
        String password = params[1].split("=")[1];
        String name = params[2].split("=")[1];
        String email = params[3].split("=")[1];

        User user = new User(userId, password, name, email);
        MemoryUserRepository.getInstance().addUser(user);

        httpResponse.redirect("/index.html");
    }
}
