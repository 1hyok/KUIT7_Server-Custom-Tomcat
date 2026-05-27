package controller;

import db.MemoryUserRepository;
import http.enums.UserQueryKey;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.util.Map;

public class SignUpController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        // 1) POST body 의 form-urlencoded 데이터를 Map 으로 파싱
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(httpRequest.getBody());

        // 2) User 인스턴스 생성 후 MemoryUserRepository 에 저장
        User user = new User(
                params.get(UserQueryKey.USER_ID.getKey()),
                params.get(UserQueryKey.PASSWORD.getKey()),
                params.get(UserQueryKey.NAME.getKey()),
                params.get(UserQueryKey.EMAIL.getKey())
        );
        MemoryUserRepository.getInstance().addUser(user);

        // 3) 회원가입 완료 후 index 페이지로 redirect (302)
        httpResponse.redirect("/index.html");
    }
}
