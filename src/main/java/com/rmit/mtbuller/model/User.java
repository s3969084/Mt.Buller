package com.rmit.mtbuller.model;

import java.util.Locale;

public class User {
    private final String firstName, lastName, username, email;
    private final String password; // TODO: Hash password
    private final Role role;

    public User(String firstName, String lastName, String username, String email, String password, Role role) {
        this.firstName = firstName; this.lastName = lastName;
        this.username = username.toLowerCase(); this.email = email;
        this.password = password; this.role = role;
    }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
}
