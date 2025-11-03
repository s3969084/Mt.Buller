package com.rmit.mtbuller.view;

import com.rmit.mtbuller.AppState;
import com.rmit.mtbuller.model.Customer;
import com.rmit.mtbuller.model.LiftPassType;
import com.rmit.mtbuller.service.PricingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class CreateBookingView extends JPanel {
    private final AppState app;
    private final RouterFrame router;

    private final JTable accomTbl = new JTable();
    private final JTable custTbl  = new JTable();

    private final JComboBox<LiftPassType> liftType = new JComboBox<>(LiftPassType.values());
    private final JSpinner liftDays   = new JSpinner(new SpinnerNumberModel(0, 0, 30, 1));
    private final JSpinner lessonCount = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));

    private final JLabel priceNightly = new JLabel("$0.00");
    private final JLabel priceAccom   = new JLabel("$0.00");
    private final JLabel priceLift    = new JLabel("$0.00");
    private final JLabel priceLesson  = new JLabel("$0.00");
    private final JLabel priceTotal   = new JLabel("$0.00");

    public CreateBookingView(AppState app, RouterFrame router) {
        this.app = app;
        this.router = router;

        setLayout(new BorderLayout(12,12));
        setBorder(new EmptyBorder(12,12,12,12));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildRight(), BorderLayout.EAST);
        add(buildFooter(), BorderLayout.SOUTH);

        styleTables();
        liftType.addActionListener(e -> liftDays.setEnabled(liftType.getSelectedItem() == LiftPassType.DAYS));
        liftDays.setEnabled(false);

        refreshCustomersTable();
        refreshAccommodationsTable(router.wizard.checkIn, router.wizard.checkOut);
        recomputePrices();
    }

    private JComponent buildHeader() {
        var p = new JPanel(new BorderLayout(8,8));
        var title = new JLabel("Select customer, accommodation, and options");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        var dates = new JLabel("Dates: " + router.wizard.checkIn + " → " + router.wizard.checkOut);
        p.add(title, BorderLayout.WEST);
        p.add(dates, BorderLayout.EAST);
        return p;
    }

    private JComponent buildCenter() {
        var accomModel = new DefaultTableModel(
                new Object[]{"ID","Type","Location","Capacity","Nightly $"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        accomTbl.setModel(accomModel);

        var custModel = new DefaultTableModel(
                new Object[]{"ID","First","Last","Email","Phone","Level"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        custTbl.setModel(custModel);

        // ✅ Customers on the left, Accommodations on the right
        var left  = wrapTitled(new JScrollPane(custTbl),  "Customers");
        var right = wrapTitled(new JScrollPane(accomTbl), "Available accommodations");

        var split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.5);
        split.setBorder(null);
        return split;
    }

    private JComponent buildRight() {
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(0,12,0,0));

        var opts = new JPanel(new GridBagLayout());
        opts.setBorder(new TitledBorder("Options"));
        var g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.anchor = GridBagConstraints.LINE_START;
        g.gridx = 0; g.gridy = 0; opts.add(new JLabel("Lift pass:"), g);
        g.gridx = 1; opts.add(liftType, g);
        g.gridx = 0; g.gridy = 1; opts.add(new JLabel("Lift days:"), g);
        g.gridx = 1; opts.add(liftDays, g);
        g.gridx = 0; g.gridy = 2; opts.add(new JLabel("Lesson count:"), g);
        g.gridx = 1; opts.add(lessonCount, g);
        panel.add(opts);

        var prices = new JPanel(new GridBagLayout());
        prices.setBorder(new TitledBorder("Price"));
        var gp = new GridBagConstraints();
        gp.insets = new Insets(4,6,4,6);
        gp.anchor = GridBagConstraints.LINE_START;
        int r = 0;
        gp.gridx = 0; gp.gridy = r; prices.add(new JLabel("Nightly:"), gp);
        gp.gridx = 1; prices.add(priceNightly, gp); r++;
        gp.gridx = 0; gp.gridy = r; prices.add(new JLabel("Accommodation:"), gp);
        gp.gridx = 1; prices.add(priceAccom, gp); r++;
        gp.gridx = 0; gp.gridy = r; prices.add(new JLabel("Lift:"), gp);
        gp.gridx = 1; prices.add(priceLift, gp); r++;
        gp.gridx = 0; gp.gridy = r; prices.add(new JLabel("Lessons:"), gp);
        gp.gridx = 1; prices.add(priceLesson, gp); r++;

        var totalLabel = new JLabel("TOTAL:");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD));
        priceTotal.setFont(priceTotal.getFont().deriveFont(Font.BOLD, 16f));
        gp.gridx = 0; gp.gridy = r; prices.add(totalLabel, gp);
        gp.gridx = 1; prices.add(priceTotal, gp);

        panel.add(Box.createVerticalStrut(8));
        panel.add(prices);

        liftType.addActionListener(e -> recomputePrices());
        liftDays.addChangeListener(e -> recomputePrices());
        lessonCount.addChangeListener(e -> recomputePrices());

        return panel;
    }

    private JComponent buildFooter() {
        var footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        var back = new JButton("Back");
        var next = new JButton("Next");
        footer.add(back);
        footer.add(next);

        back.addActionListener(e -> router.showDateSelect());
        next.addActionListener(e -> {
            router.wizard.accommodationId = selectedAccomId();
            router.wizard.customerId = selectedCustomerId();
            router.wizard.liftPassType = (LiftPassType) liftType.getSelectedItem();
            router.wizard.liftPassDays = (int) liftDays.getValue();
            router.wizard.lessonCount  = (int) lessonCount.getValue();
            router.showConfirm();
        });

        return footer;
    }

    private static JComponent wrapTitled(JComponent c, String title) {
        var p = new JPanel(new BorderLayout());
        p.setBorder(new TitledBorder(title));
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    private void styleTables() {
        for (var t : new JTable[]{accomTbl, custTbl}) {
            t.setFillsViewportHeight(true);
            t.setRowHeight(24);
            t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            t.getTableHeader().setReorderingAllowed(false);
            t.setAutoCreateRowSorter(true);
        }
    }

    private void refreshCustomersTable() {
        var m = (DefaultTableModel) custTbl.getModel();
        m.setRowCount(0);
        app.customers().listAll().forEach(c -> m.addRow(new Object[]{
                c.getId(), c.getFirstName(), c.getLastName(), c.getEmail(), c.getPhone(), c.getSkillLevel()
        }));
    }

    private void refreshAccommodationsTable(LocalDate in, LocalDate out) {
        var m = (DefaultTableModel) accomTbl.getModel();
        m.setRowCount(0);
        var all = app.inventory().listAll();
        for (var a: all) {
            boolean ok = in == null || out == null || app.bookings().isAvailable(a.getId(), in, out);
            if (ok)
                m.addRow(new Object[]{ a.getId(), a.getName(), a.getLocation(), a.getCapacity(), a.getNightly() });
        }
    }

    private int selectedAccomId() {
        int r = accomTbl.getSelectedRow();
        if (r < 0) throw new IllegalStateException("Select an accommodation");
        Object v = accomTbl.getValueAt(r, 0);
        return (v instanceof Integer) ? (Integer) v : Integer.parseInt(v.toString());
    }

    private int selectedCustomerId() {
        int r = custTbl.getSelectedRow();
        if (r < 0) throw new IllegalStateException("Select a customer");
        Object v = custTbl.getValueAt(r, 0);
        return (v instanceof Integer) ? (Integer) v : Integer.parseInt(v.toString());
    }

    private void recomputePrices() {
        try {
            int r = accomTbl.getSelectedRow();
            if (r < 0) { setPrices(0,0,0,0,0); return; }
            double nightlyDollars = Double.parseDouble(accomTbl.getValueAt(r, 4).toString());
            int nightlyCents = (int)Math.round(nightlyDollars * 100);
            long nights = java.time.temporal.ChronoUnit.DAYS.between(router.wizard.checkIn, router.wizard.checkOut);
            if (nights <= 0) { setPrices(nightlyCents,0,0,0,0); return; }
            int accomC  = PricingService.accommodationForStay(nightlyCents, nights);
            int liftC   = PricingService.liftPass((LiftPassType) liftType.getSelectedItem(), (int) liftDays.getValue());
            int lessonC = 0;
            int row = custTbl.getSelectedRow();
            if (row >= 0) {
                Object idObj = custTbl.getValueAt(row, 0);
                int custId = (idObj instanceof Integer) ? (Integer) idObj : Integer.parseInt(idObj.toString());
                var cust = app.customers().listAll().stream()
                        .filter(c -> c.getId() == custId)
                        .findFirst().orElse(null);
                if (cust != null) {
                    lessonC = PricingService.lessons(cust.getSkillLevel(), (int) lessonCount.getValue());
                }
            }
            int totalC  = PricingService.total(accomC, liftC, lessonC);
            setPrices(nightlyCents, accomC, liftC, lessonC, totalC);
        } catch (Exception ignore) { setPrices(0,0,0,0,0); }
    }

    private void setPrices(int nightlyC, int accomC, int liftC, int lessonC, int totalC) {
        priceNightly.setText(PricingService.fmtAud(nightlyC));
        priceAccom.setText(PricingService.fmtAud(accomC));
        priceLift.setText(PricingService.fmtAud(liftC));
        priceLesson.setText(PricingService.fmtAud(lessonC));
        priceTotal.setText(PricingService.fmtAud(totalC));
    }
}
