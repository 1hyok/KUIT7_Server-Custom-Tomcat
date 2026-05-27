package http.enums;

// 회원가입 / 로그인 form 에서 사용하는 쿼리 파라미터 키
public enum UserQueryKey {
    USER_ID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private final String key;

    UserQueryKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
