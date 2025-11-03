package com.rmit.mtbuller.service;

import com.rmit.mtbuller.model.Accommodation;
import com.rmit.mtbuller.repo.AccommodationRepo;

import java.util.List;

public class InventoryService {
    private final AccommodationRepo repo;

    public InventoryService(AccommodationRepo repo) { this.repo = repo; }

    public List<Accommodation> listAll() { return repo.findAllActive(); }

    public void add(String name, String location, int capacity, double nightly) {
        if (name.isBlank() || location.isBlank())
            throw new IllegalArgumentException("Name & location required");
        if (capacity <= 0)
            throw new IllegalArgumentException("Capacity must be greater than zero");
        if (nightly <= 0)
            throw new IllegalArgumentException("Nightly must be greater than zero");
        repo.insert(name, location, capacity, nightly);
    }

    public void delete(int id) { repo.deleteById(id); }

}
