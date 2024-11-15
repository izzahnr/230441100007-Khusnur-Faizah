package com.mycompany.train_system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import javax.swing.border.TitledBorder;

public class RouteManagement extends JFrame {
    private JTable routeTable;
    private DefaultTableModel tableModel;
    private JTextField routeIdField, trainIdField, departureStationIdField, arrivalStationIdField, departureTimeField, arrivalTimeField, priceField;
    private JButton createButton, updateButton, deleteButton, refreshButton;

    public RouteManagement(int adminId) {
        setTitle("Route Management");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set background color for the frame
        getContentPane().setBackground(new Color(245, 245, 245));

        // Table model for displaying route data
        String[] columnNames = {"Route ID", "Train ID", "Departure Station ID", "Arrival Station ID", "Departure Time", "Arrival Time", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0);
        routeTable = new JTable(tableModel);
        routeTable.setBackground(new Color(255, 255, 255));
        routeTable.setSelectionBackground(new Color(0, 123, 255));  // Highlight color for selected row

        // Styling form inputs
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 2), "Route Information", TitledBorder.CENTER, TitledBorder.TOP));
        formPanel.setBackground(new Color(245, 245, 245));

        formPanel.add(new JLabel("Route ID:"));
        routeIdField = new JTextField();
        routeIdField.setEditable(false);  // Route ID should not be editable
        formPanel.add(routeIdField);

        formPanel.add(new JLabel("Train ID:"));
        trainIdField = new JTextField();
        formPanel.add(trainIdField);

        formPanel.add(new JLabel("Departure Station ID:"));
        departureStationIdField = new JTextField();
        formPanel.add(departureStationIdField);

        formPanel.add(new JLabel("Arrival Station ID:"));
        arrivalStationIdField = new JTextField();
        formPanel.add(arrivalStationIdField);

        formPanel.add(new JLabel("Departure Time:"));
        departureTimeField = new JTextField();
        formPanel.add(departureTimeField);

        formPanel.add(new JLabel("Arrival Time:"));
        arrivalTimeField = new JTextField();
        formPanel.add(arrivalTimeField);

        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        // Buttons with color and tooltips
        createButton = new JButton("Create");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");

        // Apply color to buttons
        createButton.setBackground(new Color(0, 123, 255));
        createButton.setForeground(Color.WHITE);
        updateButton.setBackground(new Color(0, 123, 255));
        updateButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(40, 167, 69));
        refreshButton.setForeground(Color.WHITE);

        // Add tooltips for buttons
        createButton.setToolTipText("Create a new route");
        updateButton.setToolTipText("Update the selected route");
        deleteButton.setToolTipText("Delete the selected route");
        refreshButton.setToolTipText("Refresh route list");

        // Add buttons to the panel with custom layout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.add(createButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Add components to the main layout
        setLayout(new BorderLayout(10, 10));
        add(new JScrollPane(routeTable), BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load initial data
        loadRouteData();

        // Button listeners for CRUD operations
        createButton.addActionListener(e -> createRoute());
        updateButton.addActionListener(e -> updateRoute());
        deleteButton.addActionListener(e -> deleteRoute());
        refreshButton.addActionListener(e -> loadRouteData());
    }

    private void loadRouteData() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        tableModel.setRowCount(0); // Clear the table

        try {
            conn = DatabaseConnection.connect();
            String sql = "SELECT route_id, train_id, departure_station_id, arrival_station_id, departure_time, arrival_time, price FROM routes";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("route_id"), rs.getInt("train_id"), rs.getInt("departure_station_id"),
                        rs.getInt("arrival_station_id"), rs.getString("departure_time"),
                        rs.getString("arrival_time"), rs.getDouble("price")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading route data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void createRoute() {
        String trainId = trainIdField.getText();
        String departureStationId = departureStationIdField.getText();
        String arrivalStationId = arrivalStationIdField.getText();
        String departureTime = departureTimeField.getText();
        String arrivalTime = arrivalTimeField.getText();
        String price = priceField.getText();

        if (trainId.isEmpty() || departureStationId.isEmpty() || arrivalStationId.isEmpty() || departureTime.isEmpty() || arrivalTime.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.connect();
            String sql = "INSERT INTO routes (train_id, departure_station_id, arrival_station_id, departure_time, arrival_time, price) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(trainId));
            stmt.setInt(2, Integer.parseInt(departureStationId));
            stmt.setInt(3, Integer.parseInt(arrivalStationId));
            stmt.setString(4, departureTime);
            stmt.setString(5, arrivalTime);
            stmt.setDouble(6, Double.parseDouble(price));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Route created successfully!");
            loadRouteData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error creating route: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Update and delete methods should follow the same structure as createRoute() above

 private void updateRoute() {
        // Implement the Update operation logic
        int selectedRow = routeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int routeId = (int) tableModel.getValueAt(selectedRow, 0);
        String departureStation = departureStationIdField.getText();
        String arrivalStation = arrivalStationIdField.getText();
        String departureTime = departureTimeField.getText();
        String arrivalTime = arrivalTimeField.getText();
        String price = priceField.getText();

        if (departureStation.isEmpty() || arrivalStation.isEmpty() || departureTime.isEmpty() || arrivalTime.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.connect();
            String sql = "UPDATE routes SET departure_station = ?, arrival_station = ?, departure_time = ?, arrival_time = ?, price = ? WHERE route_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, departureStation);
            stmt.setString(2, arrivalStation);
            stmt.setString(3, departureTime);
            stmt.setString(4, arrivalTime);
            stmt.setDouble(5, Double.parseDouble(price));
            stmt.setInt(6, routeId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Route updated successfully!");
            loadRouteData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating route: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteRoute() {
        // Implement the Delete operation logic
        int selectedRow = routeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int routeId = (int) tableModel.getValueAt(selectedRow, 0);

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.connect();
            String sql = "DELETE FROM routes WHERE route_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, routeId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Route deleted successfully!");
            loadRouteData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting route: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
