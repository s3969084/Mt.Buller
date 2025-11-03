package com.rmit.mtbuller.service;

import com.rmit.mtbuller.model.Customer;
import com.rmit.mtbuller.model.LiftPassType;

import java.text.NumberFormat;
import java.util.Locale;

public final class PricingService {
    private PricingService() {}

    // ---------- Accommodation ----------
    public static int accommodationForStay(int nightlyPriceCents, long nights) {
        if (nights <= 0) return 0;
        return (int) (nightlyPriceCents * nights);
    }

    // ---------- Lift Pass ----------
    public static int liftPass(LiftPassType type, int days) {
        if (type == null || type == LiftPassType.NONE) return 0;

        if (type == LiftPassType.SEASON) {
            return 200_00; // $200 flat
        }

        // per-day rate
        int base = 26_00 * days;

        // 10% discount for 5+ days
        if (days >= 5) {
            base -= base / 10;
        }

        return base;
    }

    // ---------- Lessons ----------
    public static int lessons(Customer.LessonLevel level, int count) {
        if (count <= 0) return 0;
        int perLessonCents = switch (level) {
            case Beginner -> 25_00;
            case Intermediate -> 20_00;
            case Expert -> 15_00;
        };
        return perLessonCents * count;
    }

    // ---------- Combined ----------
    public static int total(int accomC, int liftC, int lessonC) {
        return accomC + liftC + lessonC;
    }

    // ---------- Formatting ----------
    public static String fmtAud(int cents) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(cents / 100.0);
    }
}
