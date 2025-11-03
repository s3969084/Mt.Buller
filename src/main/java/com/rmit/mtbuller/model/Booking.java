package com.rmit.mtbuller.model;

import java.time.LocalDate;

public class Booking {
    private final int id;
    private final int accommodationId;
    private final int customerId;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int nightlyPriceCents;
    private final String status;
    private final String createdAt;

    // snapshots
    private final LiftPassType liftPassType;
    private final int liftPassDays;
    private final Customer.LessonLevel lessonLevel;
    private final int lessonCount;
    private final int accomPriceCents;
    private final int liftPassPriceCents;
    private final int lessonPriceCents;
    private final int totalPriceCents;

    public Booking(int id, int accommodationId, int customerId,
                   LocalDate checkIn, LocalDate checkOut,
                   int nightlyPriceCents, String status, String createdAt,
                   LiftPassType liftPassType, int liftPassDays,
                   Customer.LessonLevel lessonLevel, int lessonCount,
                   int accomPriceCents, int liftPassPriceCents,
                   int lessonPriceCents, int totalPriceCents) {
        this.id = id;
        this.accommodationId = accommodationId;
        this.customerId = customerId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nightlyPriceCents = nightlyPriceCents;
        this.status = status;
        this.createdAt = createdAt;
        this.liftPassType = liftPassType;
        this.liftPassDays = liftPassDays;
        this.lessonLevel = lessonLevel;
        this.lessonCount = lessonCount;
        this.accomPriceCents = accomPriceCents;
        this.liftPassPriceCents = liftPassPriceCents;
        this.lessonPriceCents = lessonPriceCents;
        this.totalPriceCents = totalPriceCents;
    }

    public int getId() { return id; }
    public int getAccommodationId() { return accommodationId; }
    public int getCustomerId() { return customerId; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public int getNightlyPriceCents() { return nightlyPriceCents; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public LiftPassType getLiftPassType() { return liftPassType; }
    public int getLiftPassDays() { return liftPassDays; }
    public Customer.LessonLevel getLessonLevel() { return lessonLevel; }
    public int getLessonCount() { return lessonCount; }
    public int getAccomPriceCents() { return accomPriceCents; }
    public int getLiftPassPriceCents() { return liftPassPriceCents; }
    public int getLessonPriceCents() { return lessonPriceCents; }
    public int getTotalPriceCents() { return totalPriceCents; }
}
