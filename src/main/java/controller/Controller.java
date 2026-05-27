package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

// 모든 URL 별 작업 처리를 추상화한 컨트롤러 인터페이스.
// 각 구현체는 자신이 담당하는 URL 의 요청을 받아 응답을 생성한다.
public interface Controller {
    void execute(HttpRequest httpRequest, HttpResponse httpResponse);
}
