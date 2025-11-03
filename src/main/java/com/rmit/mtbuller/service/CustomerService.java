package com.rmit.mtbuller.service;

import com.rmit.mtbuller.model.Role;
import com.rmit.mtbuller.model.User;
import com.rmit.mtbuller.model.Customer;
import com.rmit.mtbuller.repo.CustomerRepo;

import java.util.List;

public class CustomerService {
    private final CustomerRepo repo;
    public CustomerService(CustomerRepo repo) { this.repo = repo; }

    public List<Customer> listAll() { return repo.findAll(); }

    public void add(String first_Name,String last_Name, String email, String phone, User actor) {
        ensureAdmin(actor);
        if (first_Name == null || first_Name.isBlank()) throw new IllegalArgumentException("First name required");
        if (last_Name == null || last_Name.isBlank())  throw new IllegalArgumentException("Last name required");
        if (email == null || email.isBlank() || !email.contains("@")) throw new IllegalArgumentException("Invalid email");
        if (phone == null || phone.isBlank() || !phone.matches("\\d{10}")) throw new IllegalArgumentException("Invalid phone number");
        repo.insert(first_Name, last_Name, email, phone);
    }

    public void delete(int id, User actor) {
        ensureAdmin(actor);
        repo.deleteById(id);
    }

    public Customer getById(int id) {
        return repo.listAll().stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }


    private static void ensureAdmin(User actor) {
        if (actor == null || (actor.getRole()  != Role.MASTER && actor.getRole() != Role.ADMIN))
            throw new SecurityException("Only master or admin can manage customers.");
    }
}
