package controller;

import db.MemoryUserRepository;
import http.enums.HttpHeader;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.routing.Route;

public class LoginController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = httpRequest.getBody();

        String[] params = body.split("&");
        String[] userIdParam = params[0].split("=");
        if (userIdParam.length < 2) {
            httpResponse.redirect(Route.LOGIN_FAILED.getPath());
            return;
        }
        String userId = userIdParam[1];

        boolean isSignedUp = MemoryUserRepository.getInstance().isSignedUp(userId);
        if (isSignedUp) {
            httpResponse.redirect("/index.html", HttpHeader.SET_COOKIE.getKey() + ": logined=true");
            return;
        }
        httpResponse.redirect(Route.LOGIN_FAILED.getPath());
    }
}
