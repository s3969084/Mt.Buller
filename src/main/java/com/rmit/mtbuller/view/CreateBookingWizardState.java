package com.rmit.mtbuller.view;

import com.rmit.mtbuller.model.Customer;
import com.rmit.mtbuller.model.LiftPassType;

import java.time.LocalDate;

public class CreateBookingWizardState {
    public LocalDate checkIn, checkOut;
    public Integer accommodationId, customerId;
    public LiftPassType liftPassType = LiftPassType.NONE;
    public int liftPassDays = 0;
    public Customer.LessonLevel lessonLevel = Customer.LessonLevel.Beginner;
    public int lessonCount = 0;

    public void reset() {
        checkIn = checkOut = null;
        accommodationId = accommodationId = null;
        liftPassType = liftPassType.NONE;
        liftPassDays = 0;
        lessonLevel = Customer.LessonLevel.Beginner;
        lessonCount = 0;
    }
}
