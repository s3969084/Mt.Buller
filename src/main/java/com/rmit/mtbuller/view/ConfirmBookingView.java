package com.rmit.mtbuller.view;

import com.rmit.mtbuller.AppState;
import com.rmit.mtbuller.controller.Controllers;
import com.rmit.mtbuller.model.Customer;
import com.rmit.mtbuller.model.LiftPassType;
import com.rmit.mtbuller.service.PricingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.time.temporal.ChronoUnit;

public class ConfirmBookingView extends JPanel {
    private final AppState app;
    private final RouterFrame router;

    private final JLabel summary = new JLabel();

    public ConfirmBookingView(AppState app, RouterFrame router) {
        this.app = app;
        this.router = router;

        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        var title = new JLabel("Confirm booking");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        summary.setVerticalAlignment(SwingConstants.TOP);
        add(new JScrollPane(summary), BorderLayout.CENTER);

        var btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        var back = new JButton("Back");
        var print = new JButton("Print");
        var confirm = new JButton("Confirm");
        btns.add(back);
        btns.add(print);
        btns.add(confirm);
        add(btns, BorderLayout.SOUTH);

        back.addActionListener(e -> router.showCreateBooking());
        print.addActionListener(e -> doPrint());
        confirm.addActionListener(e -> doConfirm());

        renderSummary();
    }

    private void renderSummary() {
        var w = router.wizard;

        var acc = app.inventory().listAll().stream()
                .filter(a -> a.getId() == w.accommodationId)
                .findFirst().orElse(null);

        var cust = app.customers().listAll().stream()
                .filter(c -> c.getId() == w.customerId)
                .findFirst().orElse(null);

        long nights = ChronoUnit.DAYS.between(w.checkIn, w.checkOut);
        int nightlyCents = acc == null ? 0 : (int) Math.round(acc.getNightly() * 100);
        int accomC = PricingService.accommodationForStay(nightlyCents, nights);
        int liftC = PricingService.liftPass(w.liftPassType, w.liftPassDays);

        // Use customer's stored level for lesson pricing
        int lessonC = 0;
        if (cust != null) {
            lessonC = PricingService.lessons(cust.getSkillLevel(), w.lessonCount);
        }

        int totalC = PricingService.total(accomC, liftC, lessonC);

        String html = "<html><body style='font-family:sans-serif;'>"
                + "<h3>Dates</h3>"
                + w.checkIn + " → " + w.checkOut + " (" + nights + " nights)"
                + "<h3>Customer</h3>"
                + (cust == null ? "—" : (cust.getFirstName() + " " + cust.getLastName()
                + "<br/>" + (cust.getEmail() == null ? "" : cust.getEmail())
                + (cust.getPhone() == null ? "" : " • " + cust.getPhone())))
                + "<h3>Accommodation</h3>"
                + (acc == null ? "—" : (acc.getName() + " — " + acc.getLocation() + " — cap " + acc.getCapacity()))
                + "<h3>Options</h3>"
                + "Lift: " + w.liftPassType + (w.liftPassType == LiftPassType.DAYS ? (" (" + w.liftPassDays + " days)") : "")
                + "<br/>Lessons: " + w.lessonCount
                + "<h3>Price</h3>"
                + "Nightly: " + PricingService.fmtAud(nightlyCents) + "<br/>"
                + "Accommodation: " + PricingService.fmtAud(accomC) + "<br/>"
                + "Lift: " + PricingService.fmtAud(liftC) + "<br/>"
                + "Lessons: " + PricingService.fmtAud(lessonC) + "<br/>"
                + "<b>Total: " + PricingService.fmtAud(totalC) + "</b>"
                + "</body></html>";

        summary.setText(html);
    }

    private void doPrint() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((g, pf, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) g;
            g2.translate(pf.getImageableX(), pf.getImageableY());
            this.printAll(g2);
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try {
                job.print();
            } catch (Exception ignored) {}
        }
    }

    private void doConfirm() {
        var w = router.wizard;
        try {
            app.bookings().create(
                    w.customerId, w.accommodationId,
                    w.checkIn, w.checkOut,
                    w.liftPassType, w.liftPassDays,
                    w.lessonCount
            );
            JOptionPane.showMessageDialog(this, "Booking confirmed.");
            router.wizard.reset();
            router.showBookings();
            Controllers.refreshBookings(app, router);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Confirm booking", JOptionPane.ERROR_MESSAGE);
        }
    }
}
