package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

public class HomeController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.forward("/index.html");
    }
}
