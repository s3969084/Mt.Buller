package com.rmit.mtbuller.controller;

import com.rmit.mtbuller.AppState;
import com.rmit.mtbuller.model.Role;
import com.rmit.mtbuller.model.User;
import com.rmit.mtbuller.view.RouterFrame;

import javax.swing.*;

public class DashboardController {

    public DashboardController(AppState app, RouterFrame ui) {
        // --- Logout
        ui.dashboard.logoutBtn.addActionListener(e -> {
            app.setCurrentUser(null);
            ui.showLogin();
        });

        // --- Accommodations: add
        ui.dashboard.accommodationsPanel.addBtn.addActionListener(e -> {
            try {
                var p = ui.dashboard.accommodationsPanel;
                String name = p.name.getText().trim();
                String loc  = p.location.getText().trim();
                int cap     = (int) p.capacity.getValue();
                double price= (double) p.nightly.getValue();
                app.inventory().add(name, loc, cap, price);
                Controllers.refreshAccommodations(app, ui);
                p.name.setText("");
                p.location.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ui, ex.getMessage(),
                        "Add accommodation", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- Accommodations: delete
        ui.dashboard.accommodationsPanel.deleteBtn.addActionListener(e -> {
            Integer id = ui.dashboard.accommodationsPanel.selectedId();
            if (id == null) return;
            app.inventory().delete(id);
            Controllers.refreshAccommodations(app, ui);
        });

        // --- Users: add
        ui.dashboard.usersPanel.addBtn.addActionListener(e -> {
            var cur = app.getCurrentUser();
            var v = ui.dashboard.usersPanel;
            try {
                String first = v.firstName.getText().trim();
                String last  = v.lastName.getText().trim();
                String user  = v.username.getText().trim();
                String email = v.email.getText().trim();
                String pass  = new String(v.password.getPassword());
                Role role    = (Role) v.role.getSelectedItem();

                if (first.isEmpty() || last.isEmpty() || user.isEmpty() || email.isEmpty() || pass.isEmpty())
                    throw new IllegalArgumentException("All fields required.");

                app.users().addUser(new User(first, last, user, email, pass, role), cur);
                Controllers.refreshUsers(app, ui);
                v.message.setText("User added.");
                v.firstName.setText(""); v.lastName.setText(""); v.username.setText("");
                v.email.setText(""); v.password.setText("");
            } catch (SecurityException se) {
                v.message.setText(se.getMessage());
            } catch (Exception ex) {
                v.message.setText(ex.getMessage());
            }
        });

        // --- Users: delete
        ui.dashboard.usersPanel.deleteBtn.addActionListener(e -> {
            var cur = app.getCurrentUser();
            var v = ui.dashboard.usersPanel;
            String target = v.selectedUsername();
            if (target == null) return;
            try {
                app.users().deleteUser(target, cur);
                Controllers.refreshUsers(app, ui);
                v.message.setText("User deleted.");
            } catch (SecurityException se) {
                v.message.setText(se.getMessage());
            }
        });

        // --- Customers: add
        ui.dashboard.customerPanel.addBtn.addActionListener(e -> {
            var v = ui.dashboard.customerPanel;
            try {
                var cur = app.getCurrentUser();
                app.customers().add(v.firstName.getText(), v.lastName.getText(),
                        v.email.getText(), v.phone.getText(), cur);
                Controllers.refreshCustomers(app, ui);
                v.firstName.setText(""); v.lastName.setText(""); v.email.setText(""); v.phone.setText("");
            } catch (SecurityException se) {
                v.message.setText(se.getMessage());
            } catch (Exception ex) {
                v.message.setText(ex.getMessage());
            }
        });

        // Open Wizard - step 1 (Date Selection)
        ui.dashboard.bookingsPanel.newBookingBtn.addActionListener(e -> ui.showDateSelect());
        ui.dashboard.bookingsPanel.cancelBtn.addActionListener(e -> {
            var v = ui.dashboard.bookingsPanel;
            Integer id = v.selectedId();
            if (id == null) return;
            app.bookings().cancel(id);
            Controllers.refreshBookings(app, ui);
        });
    }
}
