package com.rmit.mtbuller.view;

import javax.swing.*;
import java.awt.*;

public class DashboardView extends JPanel {
    public final JLabel  welcome   = new JLabel("Welcome!");
    public final JButton logoutBtn = new JButton("Logout");

    public final AccommodationsPanel accommodationsPanel = new AccommodationsPanel();
    public final UsersPanel usersPanel = new UsersPanel();
    public final CustomerPanel customerPanel = new CustomerPanel();
    public final BookingsPanel bookingsPanel = new BookingsPanel();



    private final JTabbedPane tabs = new JTabbedPane();

    public DashboardView() {
        setLayout(new BorderLayout(8,8));

        var top = new JPanel(new BorderLayout());
        top.add(welcome, BorderLayout.WEST);
        top.add(logoutBtn, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // Add BOTH tabs
        tabs.addTab("Accommodations", accommodationsPanel);
        tabs.addTab("Bookings", bookingsPanel);
        tabs.addTab("Customers", customerPanel);
        tabs.addTab("Users", usersPanel);


        add(tabs, BorderLayout.CENTER);
    }

    public void enableUsersTab(boolean enabled) {
        int idx = tabs.indexOfComponent(usersPanel);
        if (idx >= 0) {
            tabs.setEnabledAt(idx, enabled);
            if (!enabled && tabs.getSelectedIndex() == idx) {
                tabs.setSelectedIndex(0);
            }
        }
    }

    public void enableAccommodationsTab(boolean enabled) {
        int idx = tabs.indexOfComponent(accommodationsPanel);
        if (idx >= 0) {
            tabs.setEnabledAt(idx, enabled);
            if (!enabled && tabs.getSelectedIndex() == idx) tabs.setSelectedIndex(0);
        }
    }

    public void enableCustomersTab(boolean enabled) {
        int idx = tabs.indexOfComponent(customerPanel);
        if (idx >= 0) {
            tabs.setEnabledAt(idx, enabled);
            if (!enabled && tabs.getSelectedIndex() == idx) tabs.setSelectedIndex(0);
        }
    }

    public void enableBookingsTab(boolean enabled) {
        int idx = tabs.indexOfComponent(bookingsPanel);
        if (idx >= 0) {
            tabs.setEnabledAt(idx, enabled);
            if (!enabled && tabs.getSelectedIndex() == idx) tabs.setSelectedIndex(0);
        }
    }
}
