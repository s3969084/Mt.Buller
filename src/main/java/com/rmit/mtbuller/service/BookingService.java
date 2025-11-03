package com.rmit.mtbuller.service;

import com.rmit.mtbuller.model.Booking;
import com.rmit.mtbuller.model.Customer;
import com.rmit.mtbuller.model.LiftPassType;
import com.rmit.mtbuller.repo.AccommodationRepo;
import com.rmit.mtbuller.repo.BookingRepo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BookingService {
    private final BookingRepo bookingRepo;
    private final AccommodationRepo accommodationRepo;
    private final CustomerService customerService;

    public BookingService(BookingRepo bookingRepo, AccommodationRepo accommodationRepo, CustomerService customerService) {
        this.bookingRepo = bookingRepo;
        this.accommodationRepo = accommodationRepo;
        this.customerService = customerService;
    }

    /** Creates a new booking, automatically using the customer's stored level for lesson pricing. */
    public long create(int customerId, int accommodationId,
                       LocalDate checkIn, LocalDate checkOut,
                       LiftPassType liftPassType, int liftPassDays,
                       int lessonCount) {
        if (checkIn == null || checkOut == null)
            throw new IllegalArgumentException("Dates are required.");

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0)
            throw new IllegalArgumentException("Check-out must be after check-in.");

        if (!bookingRepo.isAccommodationAvailable(accommodationId, checkIn, checkOut))
            throw new IllegalStateException("Accommodation not available for selected dates.");

        // Retrieve customer to get their skill level
        Customer cust = customerService.getById(customerId);
        if (cust == null)
            throw new IllegalArgumentException("Invalid customer selected.");

        int nightlyCents = accommodationRepo.getBasePriceCents(accommodationId);
        int accomC = PricingService.accommodationForStay(nightlyCents, nights);
        int liftC = PricingService.liftPass(liftPassType, liftPassDays);
        int lessonC = PricingService.lessons(cust.getSkillLevel(), lessonCount);
        int totalC = PricingService.total(accomC, liftC, lessonC);

        return bookingRepo.create(
                customerId, accommodationId,
                checkIn, checkOut,
                liftPassType, liftPassDays,
                lessonCount,
                nightlyCents, accomC, liftC, lessonC, totalC
        );
    }


    public boolean isAvailable(int accommodationId, LocalDate checkIn, LocalDate checkOut) {
        return bookingRepo.isAccommodationAvailable(accommodationId, checkIn, checkOut);
    }


    public PriceQuote quote(int accommodationId, int customerId,
                            LocalDate checkIn, LocalDate checkOut,
                            LiftPassType liftPassType, int liftPassDays,
                            int lessonCount) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) nights = 0;

        Customer cust = customerService.getById(customerId);
        if (cust == null)
            throw new IllegalArgumentException("Invalid customer selected.");

        int nightlyCents = accommodationRepo.getBasePriceCents(accommodationId);
        int accomC = PricingService.accommodationForStay(nightlyCents, nights);
        int liftC = PricingService.liftPass(liftPassType, liftPassDays);
        int lessonC = PricingService.lessons(cust.getSkillLevel(), lessonCount);
        int totalC = PricingService.total(accomC, liftC, lessonC);
        return new PriceQuote(nightlyCents, (int) nights, accomC, liftC, lessonC, totalC);
    }


    public List<Booking> listAll() {
        return bookingRepo.listAll();
    }


    public void cancel(int bookingId) {
        int changed = bookingRepo.cancel(bookingId);
        if (changed == 0)
            throw new IllegalArgumentException("Booking not found or already cancelled: " + bookingId);
    }


    public static final class PriceQuote {
        private final int nightlyCents;
        private final int nights;
        private final int accomPriceCents;
        private final int liftPassPriceCents;
        private final int lessonPriceCents;
        private final int totalPriceCents;

        public PriceQuote(int nightlyCents, int nights,
                          int accomPriceCents, int liftPassPriceCents,
                          int lessonPriceCents, int totalPriceCents) {
            this.nightlyCents = nightlyCents;
            this.nights = nights;
            this.accomPriceCents = accomPriceCents;
            this.liftPassPriceCents = liftPassPriceCents;
            this.lessonPriceCents = lessonPriceCents;
            this.totalPriceCents = totalPriceCents;
        }

        public int getNightlyCents() { return nightlyCents; }
        public int getNights() { return nights; }
        public int getAccomPriceCents() { return accomPriceCents; }
        public int getLiftPassPriceCents() { return liftPassPriceCents; }
        public int getLessonPriceCents() { return lessonPriceCents; }
        public int getTotalPriceCents() { return totalPriceCents; }
    }
}
