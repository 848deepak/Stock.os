package com.inventory.view;

import com.inventory.service.AuthService;
import com.inventory.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("Inventory Management System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        add(userLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        add(passLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        add(passwordField, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                char[] passwordChars = passwordField.getPassword();
                try {
                    if (username.isEmpty() || passwordChars.length == 0) {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Username and password are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String password = new String(passwordChars);
                    User user = authService.authenticate(username, password);
                    if (user != null) {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Login successful! Role: " + user.getRole());
                        // TODO: Open dashboard based on role
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    Arrays.fill(passwordChars, '\0');
                    passwordField.setText("");
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
