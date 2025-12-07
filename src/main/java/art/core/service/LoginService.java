package art.core.service;

import art.core.exception.PasswordNotFoundException;
import art.core.exception.UserAlreadyCreatedException;
import art.core.exception.UserNotFoundException;
import art.core.model.User;
import art.core.port.UserRepository;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Getter
@Setter
public class LoginService {
    private UserRepository userRepository;
    private User currentUser;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User handleLogin(String username, String password) {
        if (userRepository.containsUser(username)) {
            return login(username, password);
        } else {
            return registration(username, password);
        }
    }
    public void setUserMap(Map<String, User> userMap) {
        userRepository.setAllUsers(userMap);
    }
    public Map<String, User> getUserMap() {
       return userRepository.findAll();
    }

    public User registration(String username, String password) {
        if (userRepository.containsUser(username)) {
            throw new UserAlreadyCreatedException("User already here");
        }
        if (username.isEmpty() || password.isEmpty() ) {
            throw new IllegalArgumentException("Username is empty");
        }

        userRepository.save(username, new User(username, password));
        currentUser = userRepository.find(username);
        return currentUser;
    }
    public User login(String username, String password) {
        User user = userRepository.find(username);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        if (!password.equals(user.getPassword())) {
            throw new PasswordNotFoundException("Wrong password");
        }
        currentUser = user;
        return user;
    }
    public void unLogin() {
        currentUser = null;
    }
    public Boolean isLoggedIn() {
        if (currentUser == null) {
            return false;
        }
        return true;
    }
}
