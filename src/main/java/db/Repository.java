package db;

import model.User;

import java.util.Collection;

// User 저장소를 위한 추상화.
// 구현체를 갈아끼울 수 있도록 인터페이스로 분리해두었다 (현재는 MemoryUserRepository).
public interface Repository {
    void addUser(User user);
    User findUserById(String id);
    Collection<User> findAll();
}