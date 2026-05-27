package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

// "/" 요청을 받아 index.html 을 반환하는 컨트롤러
public class HomeController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.forward("/index.html");
    }
}
