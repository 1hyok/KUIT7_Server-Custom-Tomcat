package http.routing;

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
