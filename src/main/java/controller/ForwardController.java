package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

public class ForwardController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.forward(httpRequest.getUrl());
    }
}
