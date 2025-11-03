package com.rmit.mtbuller.model;

public class Accommodation {
    private final int id;
    private final String name, location;
    private final int capacity;  // Number of guests
    private final double nightly; // Price per night (AUD)

    public Accommodation(int id, String name, String location, int capacity, double nightly) {
        this.id = id; this.name = name; this.location = location;
        this.capacity = capacity; this.nightly = nightly;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public double getNightly() { return nightly; }
}
