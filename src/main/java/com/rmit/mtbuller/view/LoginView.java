package com.rmit.mtbuller.view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {
    public final JTextField username = new JTextField(16);
    public final JPasswordField password = new JPasswordField(16);
    public final JButton loginBtn = new JButton("Login");
    public final JLabel message = new JLabel(" ");

    public LoginView() {
        setLayout(new GridBagLayout());
        var g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.gridx = 0; g.gridy = 0; add(new JLabel("Username:"), g);
        g.gridx = 1; add(username, g);
        g.gridx = 0; g.gridy = 1; add(new JLabel("Password:"), g);
        g.gridx = 1; add(password, g);
        g.gridx = 1; g.gridy = 2; add(loginBtn, g);
        g.gridx = 1; g.gridy = 3; add(message, g);
    }

    public void setMessage(String text) { message.setText(text); }
    public void clearMessage() { message.setText(" "); }
}
