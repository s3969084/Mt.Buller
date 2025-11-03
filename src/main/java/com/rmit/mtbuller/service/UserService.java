package com.rmit.mtbuller.service;

import com.rmit.mtbuller.model.Role;
import com.rmit.mtbuller.model.User;
import com.rmit.mtbuller.repo.UserRepo;

import java.util.List;

public class UserService {
    private final UserRepo  repo;

    public UserService(UserRepo Repo) {
        this.repo = Repo;
    }

    public List<User> listAll() {
        return repo.findAll();
    }

    public void addUser(User newUser, User actor) {
        if (repo.findByUsername(newUser.getUsername()) != null)
            throw new IllegalArgumentException("Username already exists.");

        if (newUser.getRole() == Role.ADMIN && actor.getRole() != Role.MASTER)
            throw new IllegalArgumentException("Only master can create admin accounts.");

        repo.insert(newUser);
    }

    public void deleteUser(String username, User actor) {
        if("master".equalsIgnoreCase(username))
            throw new SecurityException("Master account cannot be deleted.");

        var target = repo.findByUsername(username);
        if(target == null) return;

        if (target.getRole() == Role.ADMIN && actor.getRole() != Role.MASTER)
            throw new SecurityException("Only master can delete admin accounts.");

        repo.deleteByUsername(username);
    }
}
