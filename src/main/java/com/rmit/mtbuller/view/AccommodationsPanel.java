package com.rmit.mtbuller.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AccommodationsPanel extends JPanel {
    public final JTextField name = new JTextField(14);
    public final JTextField location = new JTextField(14);
    public final JSpinner capacity = new JSpinner(new SpinnerNumberModel(2,1,20,1));
    public final JSpinner nightly = new JSpinner(new SpinnerNumberModel(200.0, 50.0, 2000.0, 10.0));
    public final JButton addBtn = new JButton("Add");
    public final JButton deleteBtn = new JButton("Delete selected");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID","Name", "Location", "Capacity", "Nightly"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    public final JTable table = new JTable(model);

    public AccommodationsPanel() {
        setLayout(new BorderLayout(6,6));

        var form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Name:")); form.add(name);
        form.add(new JLabel("Location:")); form.add(location);
        form.add(new JLabel("Capacity:")); form.add(capacity);
        form.add(new JLabel("Nightly:")); form.add(nightly);
        form.add(addBtn); form.add(deleteBtn);
        add(form, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void setRows(Object[][] rows) {
        model.setRowCount(0);
        for (Object[] r : rows) model.addRow(r);
    }

    public Integer selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        return (Integer) table.getValueAt(row, 0);
    }
}
