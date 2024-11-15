package com.mycompany.train_system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.TableRowSorter;

public class BookingManagement extends JFrame {

    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private int adminId;

    public BookingManagement(int adminId) {
        this.adminId = adminId;

        setTitle("Booking Management");
        setSize(900, 500);  // Increased size for better visibility
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Table setup
        String[] columnNames = {"Ticket ID", "User ID", "Route ID", "Seat Number", "Booking Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        
        // Table settings
        bookingTable.setAutoCreateRowSorter(true);  // Allow sorting of table rows
        bookingTable.setRowHeight(30);  // Set row height for readability
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only one row selection at a time
        bookingTable.getTableHeader().setBackground(new Color(0, 123, 255));  // Stylish header
        bookingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14)); // Header font

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        searchPanel.add(new JLabel("Search Bookings: "));
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        // Button setup
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));

        JButton viewButton = createStyledButton("View", new Color(51, 204, 255));
        JButton updateButton = createStyledButton("Update", new Color(255, 204, 51));
        JButton deleteButton = createStyledButton("Delete", new Color(255, 99, 71));

        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        // Layout setup
        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load bookings from the database
        loadBookings();

        // Add listeners for buttons
        viewButton.addActionListener(e -> viewBooking());
        updateButton.addActionListener(e -> updateBooking());
        deleteButton.addActionListener(e -> deleteBooking());
        searchButton.addActionListener(e -> searchBookings(searchField.getText()));
    }

    // Method to create a styled button
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100, 40));
        return button;
    }

    private void loadBookings() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Establish connection (Modify connection string as needed)
            conn = DatabaseConnection.connect();

            String sql = "SELECT * FROM tickets";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("ticket_id"),
                    rs.getInt("user_id"),
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
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void searchBookings(String query) {
        DefaultTableModel model = (DefaultTableModel) bookingTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        bookingTable.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter(query));
    }

    private void viewBooking() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to view.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Retrieve data from selected row
        int ticketId = (int) tableModel.getValueAt(selectedRow, 0);
        String message = "Viewing details for Ticket ID: " + ticketId;
        JOptionPane.showMessageDialog(this, message, "View Booking", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateBooking() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ticketId = (int) tableModel.getValueAt(selectedRow, 0);
        String newSeatNumber = JOptionPane.showInputDialog(this, "Enter new seat number:", tableModel.getValueAt(selectedRow, 3));
        String newStatus = JOptionPane.showInputDialog(this, "Enter new status (Booked/Cancelled):", tableModel.getValueAt(selectedRow, 5));

        if (newSeatNumber != null && newStatus != null) {
            // Update booking in the database
            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = DatabaseConnection.connect();
                String sql = "UPDATE tickets SET seat_number = ?, status = ? WHERE ticket_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, newSeatNumber);
                stmt.setString(2, newStatus);
                stmt.setInt(3, ticketId);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Booking updated successfully!");
                    tableModel.setValueAt(newSeatNumber, selectedRow, 3);
                    tableModel.setValueAt(newStatus, selectedRow, 5);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update booking.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void deleteBooking() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ticketId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete booking with Ticket ID: " + ticketId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            // Delete booking from the database
            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = DatabaseConnection.connect();
                String sql = "DELETE FROM tickets WHERE ticket_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, ticketId);

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Booking deleted successfully!");
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete booking.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
