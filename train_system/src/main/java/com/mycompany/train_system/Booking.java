package com.mycompany.train_system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Booking extends JFrame {
    private int userId;
    private JComboBox<String> routeComboBox;
    private JTextField seatNumberField;
    private JButton bookButton;
    private JButton viewBookingsButton;

    public Booking(int userId) {
        this.userId = userId;

        setTitle("Booking");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize form components
        routeComboBox = new JComboBox<>();
        seatNumberField = new JTextField(10);
        bookButton = new JButton("Book Ticket");
        viewBookingsButton = new JButton("View My Bookings");

        // Customize the buttons
        customizeButton(bookButton);
        customizeButton(viewBookingsButton);

        // Populate routes from the database
        loadRoutes();

        // Set up layout
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Select Route:"));
        panel.add(routeComboBox);
        panel.add(new JLabel("Seat Number:"));
        panel.add(seatNumberField);
        panel.add(new JLabel()); // Placeholder for alignment
        panel.add(bookButton);
        panel.add(new JLabel()); // Placeholder for alignment
        panel.add(viewBookingsButton);

        add(panel);

        // Add button listeners
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookTicket();
            }
        });

        viewBookingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewMyBookings();
            }
        });
    }

    // Button customization method to improve styling
    private void customizeButton(JButton button) {
        button.setBackground(new Color(0, 123, 255)); // Modern blue background
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));

        // Button hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 102, 204)); // Darker blue on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 123, 255)); // Default blue
            }
        });
    }

    private void loadRoutes() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Establish connection (Adjust with your database credentials)
            conn = DatabaseConnection.connect();

            // Query to fetch routes
            String sql = "SELECT route_id, departure_station_id, arrival_station_id, departure_time, arrival_time, price FROM routes";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int routeId = rs.getInt("route_id");
                String routeInfo = "Route " + routeId + " | From: " + rs.getInt("departure_station_id")
                        + " To: " + rs.getInt("arrival_station_id") + " | Departure: " + rs.getString("departure_time")
                        + " | Arrival: " + rs.getString("arrival_time") + " | Price: $" + rs.getBigDecimal("price");
                routeComboBox.addItem(routeInfo);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading routes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Clean up resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void bookTicket() {
        String selectedRoute = (String) routeComboBox.getSelectedItem();
        String seatNumber = seatNumberField.getText().trim();

        if (selectedRoute == null || seatNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a route and enter a seat number.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int routeId = Integer.parseInt(selectedRoute.split(" ")[1]); // Assuming route_id is the second element
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // Establish connection (Adjust with your database credentials)
            conn = DatabaseConnection.connect();

            // Insert booking into tickets table
            String sql = "INSERT INTO tickets (user_id, route_id, seat_number, status) VALUES (?, ?, ?, 'Booked')";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, routeId);
            stmt.setString(3, seatNumber);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Ticket booked successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to book the ticket.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error booking ticket: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Clean up resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    void viewMyBookings() {
    // Create the main frame for viewing bookings
    JFrame bookingFrame = new JFrame("My Bookings");
    bookingFrame.setSize(800, 500);  // Increased size for better readability
    bookingFrame.setLocationRelativeTo(null);
    bookingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // Panel for holding components
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Add padding

    // Title label
    JLabel titleLabel = new JLabel("Your Bookings", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    titleLabel.setForeground(new Color(0, 123, 255)); // Stylish title color
    mainPanel.add(titleLabel, BorderLayout.NORTH);

    // Create the table for displaying bookings
    String[] columnNames = {"Ticket ID", "Route ID", "Seat Number", "Booking Date", "Status"};
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
    JTable table = new JTable(tableModel);
    
    // Make the table more readable by adjusting column widths and adding alternating row colors
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    table.setFillsViewportHeight(true);
    table.setFont(new Font("Arial", Font.PLAIN, 14));
    table.setRowHeight(30);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    // Alternating row colors
    table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            java.awt.Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (row % 2 == 0) {
                comp.setBackground(new Color(240, 240, 240)); // Light gray for even rows
            } else {
                comp.setBackground(Color.WHITE); // White for odd rows
            }
            return comp;
        }
    });
    
    // Fetch data from the database and fill the table
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
        // Establish connection (Adjust with your database credentials)
        conn = DatabaseConnection.connect();

        // SQL query to fetch bookings for the current user
        String sql = "SELECT ticket_id, route_id, seat_number, booking_date, status FROM tickets WHERE user_id = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        rs = stmt.executeQuery();

        // Add each booking to the table model
        while (rs.next()) {
            Object[] row = {
                rs.getInt("ticket_id"),
                rs.getInt("route_id"),
                rs.getString("seat_number"),
                rs.getTimestamp("booking_date"),
                rs.getString("status")
            };
            tableModel.addRow(row);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error loading bookings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        // Clean up resources
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Add the table to a scroll pane and add it to the main panel
    JScrollPane scrollPane = new JScrollPane(table);
    mainPanel.add(scrollPane, BorderLayout.CENTER);

    // Create the bottom panel for the close button
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    
    // Create the Close button
    JButton closeButton = new JButton("Close");
    closeButton.setFont(new Font("Arial", Font.BOLD, 14));
    closeButton.setBackground(new Color(255, 99, 71));  // Tomato red for close button
    closeButton.setForeground(Color.WHITE);
    closeButton.setPreferredSize(new Dimension(100, 40));
    closeButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            bookingFrame.dispose(); // Close the booking window
        }
    });
    
    bottomPanel.add(closeButton);

    // Add the bottom panel to the main frame
    mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    // Add the main panel to the frame and make it visible
    bookingFrame.add(mainPanel);
    bookingFrame.setVisible(true);
}

    public static void main(String[] args) {
        // Sample userId for demonstration
        Booking bookingForm = new Booking(1);
        bookingForm.setVisible(true);
    }
}
