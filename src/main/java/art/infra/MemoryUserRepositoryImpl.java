package art.infra;


import art.core.model.User;
import art.core.port.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserRepositoryImpl implements UserRepository {
    private Map<String, User> userMap = new HashMap<>();

    @Override
    public void save(String username, User user) {
        userMap.put(username, user);
    }

    @Override
    public User find(String username) {
        return userMap.get(username);
    }

    @Override
    public void delete(String username) {
        userMap.remove(username);
    }


    @Override
    public Map<String, User> findAll() {
        return userMap;
    }

    @Override
    public void setAllUsers(Map<String, User> users) {
        this.userMap = users;
    }

    @Override
    public boolean containsUser(String username) {
        return userMap.containsKey(username);
    }

}
