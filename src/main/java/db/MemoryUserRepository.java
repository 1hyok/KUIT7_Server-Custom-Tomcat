package db;

import model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 인메모리 User 저장소.
//
// 동시성 처리:
//  1) 저장소: HashMap 은 멀티스레드 환경에서 resize 도중 내부 상태가 망가질 수 있어
//     ConcurrentHashMap 으로 교체했다. 50개 스레드 풀에서 동시 회원가입을 처리해도 안전.
//  2) 싱글톤 초기화: "Initialization-on-demand holder" 패턴 사용.
//     - JVM 의 클래스 로딩 메커니즘이 thread-safe 를 보장한다 (JLS §12.4.2)
//     - getInstance() 호출 시점에만 Holder 클래스가 로드되어 lazy init 도 자동 보장
//     - synchronized / volatile / DCL 없이 안전 + 락 오버헤드 0
public class MemoryUserRepository implements Repository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    private MemoryUserRepository() {
    }

    // 정적 inner 클래스는 외부 클래스가 로드될 때 함께 로드되지 않고,
    // Holder.INSTANCE 가 처음 참조될 때 비로소 로드된다 → lazy init.
    private static class Holder {
        private static final MemoryUserRepository INSTANCE = new MemoryUserRepository();
    }

    public static MemoryUserRepository getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    @Override
    public User findUserById(String userId) {
        return users.get(userId);
    }

    public boolean isSignedUp(String userId) {
        return findUserById(userId) != null;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }
}
