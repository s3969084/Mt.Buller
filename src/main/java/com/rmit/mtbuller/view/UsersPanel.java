package com.rmit.mtbuller.view;

import com.rmit.mtbuller.model.Role;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UsersPanel extends JPanel {
    public final JTextField firstName = new JTextField(10);
    public final JTextField lastName = new JTextField(10);
    public final JTextField username = new JTextField(10);
    public final JTextField email = new JTextField(14);
    public final JPasswordField password = new JPasswordField(12);
    public final JComboBox<Role> role = new JComboBox<>(Role.values());
    public final JButton addBtn = new JButton("Add user");
    public final JButton deleteBtn = new JButton("Delete selected");
    public final JLabel message = new JLabel(" ");

    private final DefaultTableModel model = new DefaultTableModel( new Object[]{"Username", "First", "Last", "Email", "Role"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public final JTable table = new JTable(model);

    public UsersPanel() {
        setLayout(new BorderLayout(6,6));

        var form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("First:")); form.add(firstName);
        form.add(new JLabel("Last:")); form.add(lastName);
        form.add(new JLabel("username:")); form.add(username);
        form.add(new JLabel("Email:")); form.add(email);
        form.add(new JLabel("Password:")); form.add(password);
        form.add(new JLabel("Role:")); form.add(role);
        form.add(addBtn); form.add(deleteBtn);
        add(form, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        message.setForeground(Color.RED);
        add(message, BorderLayout.SOUTH);
    }

    public void setRows(Object[][] rows) {
        model.setRowCount(0);
        for (Object[] r : rows) model.addRow(r);
    }

    public String selectedUsername() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        return (String) table.getValueAt(row, 0);
    }
}
