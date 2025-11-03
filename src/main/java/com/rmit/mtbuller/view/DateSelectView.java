package com.rmit.mtbuller.view;

import com.rmit.mtbuller.AppState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateSelectView extends JPanel {
    private final AppState app;
    private final RouterFrame router;

    private YearMonth month = YearMonth.now();
    private final JLabel monthLbl = new JLabel("", SwingConstants.CENTER);
    private final JPanel grid = new JPanel(new GridLayout(7,7,2,2));
    private final JLabel rangeLbl = new JLabel(" ");
    private final JButton nextBtn = new JButton("Next");

    private LocalDate start = null, end = null;

    public DateSelectView(AppState app, RouterFrame router) {
        this.app = app;
        this.router = router;

        setLayout(new BorderLayout(12,12));
        setBorder(new EmptyBorder(12,12,12,12));

        var head = new JPanel(new BorderLayout(8,8));
        var title = new JLabel("Select dates");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        head.add(title, BorderLayout.WEST);
        head.add(rangeLbl, BorderLayout.EAST);
        add(head, BorderLayout.NORTH);

        var center = new JPanel(new BorderLayout(8,8));
        var nav = new JPanel(new BorderLayout());
        var prev = new JButton("◀");
        var next = new JButton("▶");
        monthLbl.setFont(monthLbl.getFont().deriveFont(Font.BOLD, 16f));
        nav.add(prev, BorderLayout.WEST);
        nav.add(monthLbl, BorderLayout.CENTER);
        nav.add(next, BorderLayout.EAST);
        center.add(nav, BorderLayout.NORTH);
        center.add(grid, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        var foot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        var cancel = new JButton("Cancel");
        nextBtn.setEnabled(false);
        foot.add(cancel);
        foot.add(nextBtn);
        add(foot, BorderLayout.SOUTH);

        prev.addActionListener(e -> { month = month.minusMonths(1); paintMonth(); });
        next.addActionListener(e -> { month = month.plusMonths(1);  paintMonth(); });

        cancel.addActionListener(e -> router.showBookings());
        nextBtn.addActionListener(e -> {
            router.wizard.checkIn  = start;
            router.wizard.checkOut = end;
            router.showCreateBooking();
        });

        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        getActionMap().put("cancel", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { router.showBookings(); }
        });

        paintMonth();
    }

    private void paintMonth() {
        grid.removeAll();
        monthLbl.setText(month.getMonth() + " " + month.getYear());

        String[] w = {"Mo","Tu","We","Th","Fr","Sa","Su"};
        for (String s : w) {
            var l = new JLabel(s, SwingConstants.CENTER);
            l.setFont(l.getFont().deriveFont(Font.BOLD));
            grid.add(l);
        }

        LocalDate first = month.atDay(1);
        int startCol = (first.getDayOfWeek().getValue() % 7); // Mon=1..Sun=7 → 0..6
        int days = month.lengthOfMonth();

        for (int i=0;i<startCol;i++) grid.add(new JLabel(" "));

        for (int d=1; d<=days; d++) {
            LocalDate date = month.atDay(d);
            var btn = new JToggleButton(String.valueOf(d));
            btn.setFocusPainted(false);
            btn.setMargin(new Insets(2,2,2,2));
            btn.setOpaque(true);
            applyRangeStyle(btn, date);
            btn.addActionListener(e -> {
                pick(date);
                updateRangeLabel();
                repaintButtons();
                nextBtn.setEnabled(validRange());
            });
            grid.add(btn);
        }

        grid.revalidate();
        grid.repaint();
        updateRangeLabel();
        nextBtn.setEnabled(validRange());
    }

    private void repaintButtons() {
        for (Component c : grid.getComponents()) {
            if (c instanceof JToggleButton b) {
                int day;
                try { day = Integer.parseInt(b.getText()); }
                catch (NumberFormatException ex) { continue; }
                applyRangeStyle(b, month.atDay(day));
            }
        }
    }

    private void applyRangeStyle(AbstractButton b, LocalDate d) {
        boolean isStart = start != null && d.equals(start);
        boolean isEnd   = end   != null && d.equals(end);
        boolean inRange = start != null && end != null && !d.isBefore(start) && !d.isAfter(end);

        Color band = new Color(216, 232, 255);
        Color edge = new Color(143, 178, 255);

        b.setBackground(inRange ? band : UIManager.getColor("Button.background"));
        b.setSelected(inRange || isStart || isEnd);
        b.setFont(b.getFont().deriveFont(isStart || isEnd ? Font.BOLD : Font.PLAIN));
        b.setBorder(BorderFactory.createLineBorder((isStart || isEnd) ? edge : new Color(200,200,200)));
    }

    private void pick(LocalDate d) {
        if (start == null || (start != null && end != null)) { start = d; end = null; }
        else if (d.isBefore(start)) { end = start; start = d; }
        else { end = d; }
    }

    private boolean validRange() {
        return start != null && end != null && end.isAfter(start);
    }

    private void updateRangeLabel() {
        DateTimeFormatter f = DateTimeFormatter.ISO_LOCAL_DATE;
        if (start == null) { rangeLbl.setText(" "); return; }
        if (end == null)   { rangeLbl.setText("Check-in: " + f.format(start)); return; }
        rangeLbl.setText(f.format(start) + "  →  " + f.format(end));
    }
}
