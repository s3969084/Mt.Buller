package com.rmit.mtbuller.view;

import com.rmit.mtbuller.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerPanel extends JPanel {
    public final JTextField firstName = new JTextField(14);
    public final JTextField lastName = new JTextField(14);
    public final JTextField email = new JTextField(14);
    public final JTextField phone = new JTextField(14);
    public final JComboBox<Customer.LessonLevel> skillLevel =
            new JComboBox<>(Customer.LessonLevel.values());

    public final JButton addBtn = new JButton("Add customer");
    public final JButton deleteBtn = new JButton("Delete selected");
    public final JLabel message = new JLabel(" ");

    private final DefaultTableModel model = new DefaultTableModel( new Object[]{"ID", "First", "Last", "Email", "Phone"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public final JTable table = new JTable(model);

    public CustomerPanel() {
        setLayout(new BorderLayout(6,6));

        var form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("First name:")); form.add(firstName);
        form.add(new JLabel("Last name:")); form.add(lastName);
        form.add(new JLabel("Email:")); form.add(email);
        form.add(new JLabel("Phone:")); form.add(phone);
        form.add(new JLabel("Skill level:"));
        form.add(skillLevel);

        form.add(addBtn); form.add(deleteBtn);
        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        message.setForeground(Color.RED);
        add(message, BorderLayout.SOUTH);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void setRows(Object[][] rows) {
        model.setRowCount(0);
        for (Object[] r : rows) model.addRow(r);
    }

    public Integer selectedId() {
        int row = table.getSelectedRow();
        return row < 0 ? null : (Integer) table.getValueAt(row, 0);
    }
}
