package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;
import http.routing.Route;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.getCookie().contains("logined=true")) {
            httpResponse.forward("/user/list.html");
            return;
        }
        httpResponse.redirect(Route.LOGIN_PAGE.getPath());
    }
}
