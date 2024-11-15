package com.mycompany.train_system;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Arrays;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginFrame() {
        setTitle("Train Ticket System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Set custom background color
        getContentPane().setBackground(new Color(245, 245, 245));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        

        // Title
        JLabel titleLabel = new JLabel("Login to Train System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 51, 102));  // Dark blue color
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        // Add some vertical spacing
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Username
        JPanel usernamePanel = createFieldPanel("Username:", usernameField = new JTextField(20));
        mainPanel.add(usernamePanel);

        // Password
        JPanel passwordPanel = createFieldPanel("Password:", passwordField = new JPasswordField(20));
        mainPanel.add(passwordPanel);

        // Add some vertical spacing
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        
        // Customize buttons with icons and color
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(153, 153, 153));
        registerButton.setForeground(Color.WHITE);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel);

        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegistration());

        add(mainPanel);
    }

    private JPanel createFieldPanel(String label, JTextField field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(245, 245, 245));

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        panel.add(jLabel);
        panel.add(field);

        return panel;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password); // In real application, use password hashing

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("user_id");

                this.dispose(); // Close login window

                if ("admin".equals(role)) {
                    new AdminDashboard(userId).setVisible(true);
                } else {
                    new UserDashboard(userId).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegistration() {
        this.dispose();
        new RegistrationFrame().setVisible(true);
    }
}

class RegistrationFrame extends JFrame {
    private JTextField usernameField, emailField, fullNameField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, backButton;

    public RegistrationFrame() {
        setTitle("Train Ticket System - Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        // Set custom background color
        getContentPane().setBackground(new Color(245, 245, 245));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("User Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 51, 102));  // Dark blue color
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        // Add some vertical spacing
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Fields
        int row = 1;
        mainPanel.add(createFieldPanel("Username:", usernameField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Password:", passwordField = new JPasswordField(20)));
        mainPanel.add(createFieldPanel("Confirm Password:", confirmPasswordField = new JPasswordField(20)));
        mainPanel.add(createFieldPanel("Email:", emailField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Full Name:", fullNameField = new JTextField(20)));
        mainPanel.add(createFieldPanel("Phone:", phoneField = new JTextField(20)));

        // Add some vertical spacing
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");

        // Customize buttons with colors
        registerButton.setBackground(new Color(0, 102, 204));
        registerButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(153, 153, 153));
        backButton.setForeground(Color.WHITE);

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel);

        // Add action listeners
        registerButton.addActionListener(e -> handleRegistration());
        backButton.addActionListener(e -> returnToLogin());

        add(mainPanel);
    }

    private JPanel createFieldPanel(String label, JTextField field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(245, 245, 245));

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        field.setPreferredSize(new Dimension(200, 30));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        panel.add(jLabel);
        panel.add(field);

        return panel;
    }

    private void handleRegistration() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            String query = "INSERT INTO users (username, password, email, full_name, phone_number, role) " +
                          "VALUES (?, ?, ?, ?, ?, 'user')";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, usernameField.getText());
            pstmt.setString(2, new String(passwordField.getPassword())); // In real app, use password hashing
            pstmt.setString(3, emailField.getText());
            pstmt.setString(4, fullNameField.getText());
            pstmt.setString(5, phoneField.getText());

            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Registration successful! Please login.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            returnToLogin();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Registration failed: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInputs() {
        // Check empty fields
        if (usernameField.getText().isEmpty() ||
            emailField.getText().isEmpty() ||
            fullNameField.getText().isEmpty() ||
            phoneField.getText().isEmpty() ||
            passwordField.getPassword().length == 0 ||
            confirmPasswordField.getPassword().length == 0) {

            JOptionPane.showMessageDialog(this,
                "All fields must be filled.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if passwords match
        if (!Arrays.equals(passwordField.getPassword(), confirmPasswordField.getPassword())) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void returnToLogin() {
        this.dispose();
        new LoginFrame().setVisible(true);
    }
}
