package com.rmit.mtbuller.repo;


import com.rmit.mtbuller.model.User;
import com.rmit.mtbuller.db.RowMapper;
import com.rmit.mtbuller.db.Table;
import com.rmit.mtbuller.model.Role;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;

public class UserRepo {
    private final Table users = new Table("users");

    private static final RowMapper<User> M = new RowMapper<>() {
        @Override public User map(ResultSet rs) throws SQLException {
            return new User(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    Role.valueOf(rs.getString("role"))
            );
        }
    };

    public User findByUsername(String username) {
        var list = users.select(
                "username, first_name, last_name, email, password_hash, role",
                "WHERE username=?",
                M, username.toLowerCase());
        return list.isEmpty() ? null : list.get(0);
    }

    public List<User> findAll() {
        return users.select(
                "username, first_name, last_name, email, password_hash, role",
                "ORDER BY username", M);
    }

    public void insert(User u) {
        var v = new LinkedHashMap<String, Object>();
        v.put("username", u.getUsername());
        v.put("first_name", u.getFirstName());
        v.put("last_name", u.getLastName());
        v.put("email", u.getEmail());
        v.put("password_hash", u.getPassword());   // Bind password (plain)
        v.put("role", u.getRole());
        users.insert(v);
    }

    public void deleteByUsername(String username) {
        users.delete("username=?", username.toLowerCase());
    }
}
