package webserver;

import controller.*;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.routing.Route;

import java.util.HashMap;
import java.util.Map;

// URL 과 Controller 를 매핑해주는 책임을 갖는 클래스.
// RequestHandler 에서 if 분기로 컨트롤러를 고르던 책임을 분리했다.
public class RequestMapper {
    // URL 별 컨트롤러를 미리 등록해두는 정적 매핑 테이블
    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new HomeController());
        controllers.put(Route.SIGNUP.getPath(), new SignUpController());
        controllers.put(Route.LOGIN.getPath(), new LoginController());
        controllers.put(Route.USER_LIST.getPath(), new ListController());
    }

    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    // 요청 URL 에 매핑된 컨트롤러를 찾아 실행한다.
    // 매핑이 없으면 정적 리소스 응답을 처리하는 ForwardController 를 사용한다.
    public void proceed() {
        String url = httpRequest.getUrl();
        Controller controller = controllers.getOrDefault(url, new ForwardController());
        controller.execute(httpRequest, httpResponse);
    }
}
