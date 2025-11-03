package com.rmit.mtbuller;

import com.rmit.mtbuller.db.Bootstrap;
import com.rmit.mtbuller.model.User;
import com.rmit.mtbuller.repo.AccommodationRepo;
import com.rmit.mtbuller.repo.BookingRepo;
import com.rmit.mtbuller.repo.CustomerRepo;
import com.rmit.mtbuller.repo.UserRepo;
import com.rmit.mtbuller.service.AuthService;
import com.rmit.mtbuller.service.BookingService;
import com.rmit.mtbuller.service.CustomerService;
import com.rmit.mtbuller.service.InventoryService;
import com.rmit.mtbuller.service.UserService;

public class AppState {
    private final AuthService auth;
    private final UserService users;
    private final InventoryService inventory;
    private final CustomerService customers;
    private final BookingService bookings;

    private User currentUser;

    public AppState() {
        Bootstrap.ensureSchemaAndSeed();

        // Repos
        var userRepo  = new UserRepo();
        var accRepo   = new AccommodationRepo();
        var custRepo  = new CustomerRepo();
        var bookRepo  = new BookingRepo();

        // Services
        this.auth      = new AuthService(userRepo);
        this.users     = new UserService(userRepo);
        this.inventory = new InventoryService(accRepo);
        this.customers = new CustomerService(custRepo);
        this.bookings  = new BookingService(bookRepo, accRepo, customers);
    }

    // Accessors
    public AuthService auth()            { return auth; }
    public UserService users()           { return users; }
    public InventoryService inventory()  { return inventory; }
    public CustomerService customers()   { return customers; }
    public BookingService bookings()     { return bookings; }

    public User getCurrentUser()         { return currentUser; }
    public void setCurrentUser(User u)   { this.currentUser = u; }
}
