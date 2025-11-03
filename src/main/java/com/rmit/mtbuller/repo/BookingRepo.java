package com.rmit.mtbuller.repo;

import com.rmit.mtbuller.db.RowMapper;
import com.rmit.mtbuller.db.Table;
import com.rmit.mtbuller.model.Booking;
import com.rmit.mtbuller.model.Customer;
import com.rmit.mtbuller.model.LiftPassType;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

public class BookingRepo {
    private final Table bookings = new Table("bookings");

    private final RowMapper<Booking> M = rs -> new Booking(
            rs.getInt("id"),
            rs.getInt("accommodation_id"),
            rs.getInt("customer_id"),
            LocalDate.parse(rs.getString("check_in")),
            LocalDate.parse(rs.getString("check_out")),
            rs.getInt("nightly_price_cents"),
            rs.getString("status"),
            rs.getString("created_at"),
            parseLiftPass(rs.getString("lift_pass_type")),
            rs.getInt("lift_pass_days"),
            parseLevel(rs.getString("lesson_level")),
            rs.getInt("lesson_count"),
            rs.getInt("accom_price_cents"),
            rs.getInt("lift_pass_price_cents"),
            rs.getInt("lesson_price_cents"),
            rs.getInt("total_price_cents")
    );

    private static LiftPassType parseLiftPass(String t) {
        if (t == null) return LiftPassType.NONE;
        try { return LiftPassType.valueOf(t); } catch (Exception e) { return LiftPassType.NONE; }
    }
    private static Customer.LessonLevel parseLevel(String t) {
        if (t == null) return Customer.LessonLevel.Beginner;
        try { return Customer.LessonLevel.valueOf(t); } catch (Exception e) { return Customer.LessonLevel.Beginner; }
    }

    /** True if no overlapping booking exists for the accommodation in [checkIn, checkOut). */
    public boolean isAccommodationAvailable(int accommodationId, LocalDate checkIn, LocalDate checkOut) {
        // Overlap if NOT (existing.check_out <= :in OR existing.check_in >= :out)
        var a = bookings.select("COUNT(1) AS cnt",
                "WHERE accommodation_id = ? AND NOT (check_out <= ? OR check_in >= ?)",
                rs -> rs.getInt("cnt"),
                accommodationId, checkIn.toString(), checkOut.toString()
        );
        int conflicts = a.isEmpty() ? 0 : a.get(0);
        return conflicts == 0;
    }

    public long create(int customerId, int accommodationId,
                       LocalDate checkIn, LocalDate checkOut,
                       LiftPassType liftPassType, int liftPassDays,
                       int lessonCount,
                       int nightlyPriceCents, int accomPriceCents,
                       int liftPassPriceCents, int lessonPriceCents, int totalPriceCents) {

        var v = new LinkedHashMap<String, Object>();
        v.put("customer_id", customerId);
        v.put("accommodation_id", accommodationId);
        v.put("check_in", checkIn.toString());
        v.put("check_out", checkOut.toString());
        v.put("nightly_price_cents", nightlyPriceCents);
        v.put("status", "CONFIRMED");
        v.put("lift_pass_type", liftPassType.name());
        v.put("lift_pass_days", liftPassDays);
        v.put("lesson_count", lessonCount);
        v.put("accom_price_cents", accomPriceCents);
        v.put("lift_pass_price_cents", liftPassPriceCents);
        v.put("lesson_price_cents", lessonPriceCents);
        v.put("total_price_cents", totalPriceCents);

        return bookings.insert(v);
    }


    public List<Booking> listAll() {
        return bookings.select("*", "ORDER BY created_at DESC, id DESC", M);
    }

    public List<Booking> findAllOrderByDateDesc() {
        return bookings.select("*", "ORDER BY created_at DESC, id DESC", M);
    }

    public int cancel(int id) {
        // returns number of rows updated (0 if not found)
        return bookings.update("status = ?", "id = ?", "CANCELLED", id);
    }
}
