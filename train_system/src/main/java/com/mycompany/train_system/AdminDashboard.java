package com.mycompany.train_system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboard extends JFrame {
    private int adminId;

    public AdminDashboard(int adminId) {
        this.adminId = adminId;
        setTitle("Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set the background color
        getContentPane().setBackground(new Color(245, 245, 245));

        // Create the main panel with a custom layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(80, 152, 212));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create buttons with enhanced styling
        JButton manageTrainsButton = createStyledButton("Manage Trains", new ImageIcon("train_icon.png"));
        JButton manageRoutesButton = createStyledButton("Manage Routes", new ImageIcon("route_icon.png"));
        JButton viewBookingsButton = createStyledButton("View All Bookings", new ImageIcon("booking_icon.png"));

        // Add Action Listeners
        manageTrainsButton.addActionListener(e -> openTrainManagement());
        manageRoutesButton.addActionListener(e -> openRouteManagement());
        viewBookingsButton.addActionListener(e -> openBookings());

        // Add buttons to the panel with GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(manageTrainsButton, gbc);

        gbc.gridy = 1;
        mainPanel.add(manageRoutesButton, gbc);

        gbc.gridy = 2;
        mainPanel.add(viewBookingsButton, gbc);

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Icon icon) {
        JButton button = new JButton(text, icon);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 123, 255)); // Blue color
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(200, 50));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        button.setBorderPainted(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);  // Align text and icon to the left

        // Add padding around text and icon
        button.setIconTextGap(10);
        
        // Add rounded corners
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        
        return button;
    }

    private void openTrainManagement() {
        new TrainManagement(adminId).setVisible(true);
    }

    private void openRouteManagement() {
        new RouteManagement(adminId).setVisible(true);
    }

    private void openBookings() {
        new BookingManagement(adminId).setVisible(true);
    }
}
