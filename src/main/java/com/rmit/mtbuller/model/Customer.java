package com.rmit.mtbuller.model;

import java.util.Objects;

public class Customer {
    public enum LessonLevel { Beginner, Intermediate, Expert }

    private final int id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final LessonLevel skillLevel;

    public Customer(int id,
                    String firstName,
                    String lastName,
                    String email,
                    String phone,
                    LessonLevel skillLevel) {
        this.id = id;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.phone     = phone;
        this.skillLevel = skillLevel == null ? LessonLevel.Beginner : skillLevel;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LessonLevel getSkillLevel() { return skillLevel; }

    @Override public String toString() {
        return firstName + " " + lastName + "  •  " +
                (email != null ? email : "") +
                (phone != null && !phone.isBlank() ? "  •  " + phone : "") +
                "  •  " + skillLevel;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        return id == ((Customer) o).id;
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
