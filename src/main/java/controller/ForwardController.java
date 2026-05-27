package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

// 매핑된 URL 이 없을 때 사용되는 기본 컨트롤러.
// 정적 리소스(html, css, js, 이미지 등) 요청을 그대로 forward 처리한다.
public class ForwardController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.forward(httpRequest.getUrl());
    }
}
