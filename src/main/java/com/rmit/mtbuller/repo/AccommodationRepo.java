package com.rmit.mtbuller.repo;

import com.rmit.mtbuller.db.RowMapper;
import com.rmit.mtbuller.db.Table;
import com.rmit.mtbuller.model.Accommodation;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class AccommodationRepo {
    private final Table accommodations = new Table("accommodations");

    private static final RowMapper<Accommodation> M = rs -> {
        int cents = rs.getInt("base_price_cents");
        return new Accommodation(
                rs.getInt("id"),
                rs.getString("property_type"),
                rs.getString("location"),
                rs.getInt("capacity"),
                cents / 100.0  // nightly in dollars for the UI/model
        );
    };


    public List<Accommodation> findAllActive() {
        return accommodations.select(
                "id, property_type, location, capacity, base_price_cents",
                "WHERE status='ACTIVE' ORDER BY id",
                M
        );
    }

    public Optional<Accommodation> findById(int id) {
        var rows = accommodations.select(
                "id, property_type, location, capacity, base_price_cents",
                "WHERE id = ? LIMIT 1",
                M, id
        );
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }


    public int getBasePriceCents(int accommodationId) {
        var rows = accommodations.select(
                "base_price_cents",
                "WHERE id = ? LIMIT 1",
                rs -> rs.getInt(1),
                accommodationId
        );
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("Accommodation not found: " + accommodationId);
        }
        return rows.get(0);
    }


    public void insert(String propertyType, String roomType, String location, int capacity, int basePriceCents) {
        var v = new LinkedHashMap<String, Object>();
        v.put("property_type", propertyType);
        v.put("room_type",     roomType);
        v.put("capacity",      capacity);
        v.put("base_price_cents", basePriceCents);
        v.put("location",      location);
        v.put("status",        "ACTIVE");
        accommodations.insert(v);
    }


    public void insert(String propertyType, String roomType, String location, int capacity, double nightlyDollars) {
        int cents = new BigDecimal(nightlyDollars).movePointRight(2).setScale(0, BigDecimal.ROUND_HALF_UP).intValueExact();
        insert(propertyType, roomType, location, capacity, cents);
    }

    public void insert(String name, String location, int capacity, double nightlyDollars) {
        // Preserve old behavior but route through the precise insert
        insert(name, name, location, capacity, nightlyDollars);
    }


    public void deleteById(int id) {
        accommodations.delete("id=?", id);
    }
}
