package com.rmit.mtbuller.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BookingsPanel extends JPanel {
    private final JTable table = new JTable();
    public final JButton newBookingBtn = new JButton("New Booking");
    public final JButton cancelBtn    = new JButton("Cancel Booking");
    private final DefaultTableModel model;

    public BookingsPanel() {
        setLayout(new BorderLayout(8,8));

        model = new DefaultTableModel(new Object[]{
                "ID","Customer","Accommodation","Check-in","Check-out","Status",
                "Nightly","Accom $","Lift","Lift $","Lessons","Lessons $","Total $","Created"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setModel(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(newBookingBtn);
        actions.add(cancelBtn);
        add(actions, BorderLayout.SOUTH);
    }

    public void setRows(Object[][] rows) {
        model.setRowCount(0);
        for (Object[] r : rows) model.addRow(r);
    }

    public Integer selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        Object v = table.getValueAt(row, 0);
        return (v instanceof Integer) ? (Integer) v : Integer.valueOf(v.toString());
    }
}
