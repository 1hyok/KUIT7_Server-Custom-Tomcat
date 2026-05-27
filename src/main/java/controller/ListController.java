package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;
import http.routing.Route;

// 유저 목록 페이지 컨트롤러.
// Cookie 의 logined=true 여부로 로그인 상태를 판별한다.
public class ListController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        // 1) 로그인 되어 있으면 user list 화면 forward
        if (httpRequest.getCookie().contains("logined=true")) {
            httpResponse.forward("/user/list.html");
            return;
        }

        // 2) 비로그인 상태면 login 페이지로 redirect
        httpResponse.redirect(Route.LOGIN_PAGE.getPath());
    }
}
