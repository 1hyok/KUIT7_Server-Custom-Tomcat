package http.routing;

// 애플리케이션에서 사용하는 URL 경로를 한 곳에서 관리하는 enum
public enum Route {
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    USER_LIST("/user/userList"),
    LOGIN_PAGE("/user/login.html"),
    LOGIN_FAILED("/user/login_failed.html");

    private final String path;

    Route(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
