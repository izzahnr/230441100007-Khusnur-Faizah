package com.mycompany.train_system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserDashboard extends JFrame {
    private int userId;

    public UserDashboard(int userId) {
        this.userId = userId;
        setTitle("User Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set the background color for the window
        getContentPane().setBackground(new Color(245, 245, 245));

        // Create the main panel with a custom layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(80, 152, 212));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add a label at the top for the user's dashboard title
        JLabel titleLabel = new JLabel("Welcome to User Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        // Add some vertical space between the title and the buttons
        mainPanel.add(Box.createVerticalStrut(20));

        // Create buttons with enhanced styling
        JButton bookTicketButton = createStyledButton("Book Ticket", new ImageIcon("ticket_icon.png"));
        JButton viewMyBookingsButton = createStyledButton("View My Bookings", new ImageIcon("view_bookings_icon.png"));

        // Add action listeners
        bookTicketButton.addActionListener(e -> bookTicket());
        viewMyBookingsButton.addActionListener(e -> viewBookings());

        // Add buttons to the panel
        mainPanel.add(bookTicketButton);
        mainPanel.add(Box.createVerticalStrut(15)); // Space between buttons
        mainPanel.add(viewMyBookingsButton);

        // Add the main panel to the frame
        add(mainPanel);
    }

    // Method to create a styled button with text and an icon
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
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button

        // Add padding around the text and icon
        button.setIconTextGap(10);
        
        // Add rounded corners to the button
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        
        return button;
    }

    // Action method for booking a ticket
    private void bookTicket() {
        new Booking(userId).setVisible(true);
        // You can add more functionality as needed here
    }

    // Action method for viewing the user's bookings
    private void viewBookings() {
        Booking booking = new Booking(userId); // Create a Booking instance
        booking.viewMyBookings(); // Call the method to display the user's bookings
    }

    // Main method to run the UserDashboard for testing purposes
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserDashboard(1).setVisible(true));
    }
}
