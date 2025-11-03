package com.rmit.mtbuller.controller;

import com.rmit.mtbuller.AppState;
import com.rmit.mtbuller.model.Accommodation;
import com.rmit.mtbuller.model.Booking;
import com.rmit.mtbuller.model.Customer;
import com.rmit.mtbuller.model.User;
import com.rmit.mtbuller.service.PricingService;
import com.rmit.mtbuller.view.RouterFrame;

import java.util.List;

public class Controllers {

    static void refreshAccommodations(AppState app, RouterFrame ui) {
        List<Accommodation> list = app.inventory().listAll();
        Object[][] rows = list.stream()
                .map(a -> new Object[]{ a.getId(), a.getName(), a.getLocation(), a.getCapacity(), a.getNightly() })
                .toArray(Object[][]::new);
        ui.dashboard.accommodationsPanel.setRows(rows);
    }

    static void refreshUsers(AppState app, RouterFrame ui) {
        List<User> list = app.users().listAll();
        Object[][] rows = list.stream()
                .map(u -> new Object[]{ u.getUsername(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getRole().name() })
                .toArray(Object[][]::new);
        ui.dashboard.usersPanel.setRows(rows);
    }

    static void refreshCustomers(AppState app, RouterFrame ui) {
        List<Customer> list = app.customers().listAll();
        Object[][] rows = list.stream()
                .map(c -> new Object[]{
                        c.getId(), c.getFirstName(), c.getLastName(), c.getEmail(), c.getPhone(), c.getSkillLevel()
                })
                .toArray(Object[][]::new);
        ui.dashboard.customerPanel.setRows(rows);
        // no more combo population on BookingsPanel — CreateBookingView owns selection now
    }

    public static void refreshBookings(AppState app, RouterFrame ui) {
        List<Booking> list = app.bookings().listAll();
        Object[][] rows = list.stream()
                .map(b -> new Object[]{
                        b.getId(),
                        b.getCustomerId(),
                        b.getAccommodationId(),
                        b.getCheckIn(),
                        b.getCheckOut(),
                        b.getStatus(),
                        PricingService.fmtAud(b.getNightlyPriceCents()),
                        PricingService.fmtAud(b.getAccomPriceCents()),
                        b.getLiftPassType() + (b.getLiftPassType().name().equals("DAYS") ? " (" + b.getLiftPassDays() + "d)" : ""),
                        PricingService.fmtAud(b.getLiftPassPriceCents()),
                        b.getLessonCount(),
                        PricingService.fmtAud(b.getLessonPriceCents()),
                        PricingService.fmtAud(b.getTotalPriceCents()),
                        b.getCreatedAt()
                })
                .toArray(Object[][]::new);
        ui.dashboard.bookingsPanel.setRows(rows);
    }
}
