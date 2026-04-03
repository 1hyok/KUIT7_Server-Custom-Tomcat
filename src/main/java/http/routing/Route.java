package http.routing;

public enum Route {
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    USER_LIST("/user/userList");

    private final String path;

    Route(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
