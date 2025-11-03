package com.rmit.mtbuller.controller;

import com.rmit.mtbuller.AppState;
import com.rmit.mtbuller.model.Role;
import com.rmit.mtbuller.view.RouterFrame;

public class LoginController {
    public LoginController(AppState app, RouterFrame ui) {
        ui.login.loginBtn.addActionListener(e -> {
            var username = ui.login.username.getText().trim();
            var password = new String(ui.login.password.getPassword());

            var user = app.auth().login(username, password);
            if (user == null) {
                ui.login.message.setText("Invalid credentials.");
                return;
            }

            app.setCurrentUser(user);

            boolean canManageUsers = user.getRole() == Role.MASTER || user.getRole() == Role.ADMIN;
            ui.dashboard.enableUsersTab(canManageUsers);
            ui.dashboard.enableCustomersTab(canManageUsers);

            Controllers.refreshAccommodations(app, ui);
            Controllers.refreshUsers(app, ui);
            Controllers.refreshCustomers(app, ui);
            Controllers.refreshBookings(app, ui);

            ui.login.message.setText(" ");
            ui.showDashboard();
        });
    }
}
