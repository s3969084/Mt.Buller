package com.rmit.mtbuller.view;

import com.rmit.mtbuller.AppState;

import javax.swing.*;
import java.awt.*;

public class RouterFrame extends JFrame {
    private final AppState app;

    public final DashboardView dashboard;
    public final LoginView login;
    public final CreateBookingWizardState wizard = new CreateBookingWizardState();

    public RouterFrame(AppState app) {
        super("Mt Buller Winter Resort - Reservations");
        this.app = app;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 700));
        this.dashboard = new DashboardView();
        this.login     = new LoginView();
        setContent(login);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void showLogin()       { setContent(login); }
    public void showDashboard()   { setContent(dashboard); }
    public void showBookings()    { setContent(dashboard); }

    public void showDateSelect()  { setContent(new DateSelectView(app, this)); }
    public void showCreateBooking(){ setContent(new CreateBookingView(app, this)); }
    public void showConfirm()     { setContent(new ConfirmBookingView(app, this)); }

    public AppState app() { return app; }

    private void setContent(JComponent comp) {
        setContentPane(comp);
        invalidate(); validate(); repaint();
    }
}
