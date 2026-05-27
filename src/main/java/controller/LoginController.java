package controller;

import db.MemoryUserRepository;
import http.enums.HttpHeader;
import http.enums.UserQueryKey;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.routing.Route;
import http.util.HttpRequestUtils;
import model.User;

import java.util.Map;

public class LoginController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        // 1) request body 의 쿼리스트링을 Map 으로 파싱
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(httpRequest.getBody());
        String userId = params.get(UserQueryKey.USER_ID.getKey());
        String password = params.get(UserQueryKey.PASSWORD.getKey());

        // 2) userId / password 가 모두 일치해야 로그인 성공
        User user = MemoryUserRepository.getInstance().findUserById(userId);
        if (user != null && user.getPassword().equals(password)) {
            // 로그인 성공 시 Set-Cookie 와 함께 index 로 redirect
            httpResponse.redirect("/index.html", HttpHeader.SET_COOKIE.getKey() + ": logined=true");
            return;
        }

        // 3) 실패 시 login_failed 페이지로 redirect
        httpResponse.redirect(Route.LOGIN_FAILED.getPath());
    }
}
