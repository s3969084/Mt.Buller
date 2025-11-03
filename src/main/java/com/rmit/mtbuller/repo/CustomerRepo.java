package com.rmit.mtbuller.repo;

import com.rmit.mtbuller.db.RowMapper;
import com.rmit.mtbuller.db.Table;
import com.rmit.mtbuller.model.Customer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class CustomerRepo {
    private final Table customers = new Table("customers");

    private final RowMapper<Customer> M = this::map;

    private Customer map(java.sql.ResultSet rs) throws java.sql.SQLException {
        String lvl = rs.getString("skill_level");
        Customer.LessonLevel level;
        try {
            level = (lvl == null || lvl.isBlank()) ? Customer.LessonLevel.Beginner
                    : Customer.LessonLevel.valueOf(lvl);
        } catch (IllegalArgumentException ex) {
            level = Customer.LessonLevel.Beginner;
        }
        return new Customer(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("phone"),
                level
        );
    }

    public List<Customer> findAll() {
        return customers.select(
                "id, first_name, last_name, email, phone, skill_level",
                "ORDER BY id ASC",
                M
        );
    }

    public List<Customer> listAll() {
        return findAll();
    }

    public Optional<Customer> findById(int id) {
        List<Customer> rows = customers.select(
                "id, first_name, last_name, email, phone, skill_level",
                "WHERE id = ? LIMIT 1",
                M, id
        );
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }


    public List<Customer> searchAllColumns(String query) {
        String like = "%" + (query == null ? "" : query.trim()) + "%";
        return customers.select(
                "id, first_name, last_name, email, phone, skill_level",
                "WHERE LOWER(first_name) LIKE LOWER(?) " +
                        "   OR LOWER(last_name)  LIKE LOWER(?) " +
                        "   OR LOWER(email)      LIKE LOWER(?) " +
                        "   OR LOWER(phone)      LIKE LOWER(?) " +
                        "ORDER BY last_name ASC, first_name ASC, id ASC",
                M, like, like, like, like
        );
    }


    public void insert(String firstName, String lastName, String email, String phone, Customer.LessonLevel skill) {
        var v = new LinkedHashMap<String, Object>();
        v.put("first_name", firstName);
        v.put("last_name",  lastName);
        v.put("email",      email);
        v.put("phone",      phone);
        v.put("skill_level", (skill == null ? Customer.LessonLevel.Beginner : skill).name());
        customers.insert(v);
    }


    public void insert(String firstName, String lastName, String email, String phone) {
        insert(firstName, lastName, email, phone, Customer.LessonLevel.Beginner);
    }

    public void deleteById(int id) {
        customers.delete("id=?", id);
    }
}
