package com.rmit.mtbuller;

import com.rmit.mtbuller.controller.DashboardController;
import com.rmit.mtbuller.controller.LoginController;
import com.rmit.mtbuller.view.RouterFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var app = new AppState();
            var ui  = new RouterFrame(app);

            new LoginController(app, ui);
            new DashboardController(app, ui);

            ui.pack();
            ui.setLocationRelativeTo(null);
            ui.setVisible(true);
        });
    }
}
