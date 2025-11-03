package com.rmit.mtbuller.service;


import com.rmit.mtbuller.model.User;
import com.rmit.mtbuller.repo.UserRepo;

public class AuthService {
    private final UserRepo users;

    public AuthService(UserRepo users) {
        this.users = users;
    }

    public User login(String username, String password) {
        User u = users.findByUsername(username);
        return (u != null && u.getPassword().equals(password))
                ? u : null;
    }
}
