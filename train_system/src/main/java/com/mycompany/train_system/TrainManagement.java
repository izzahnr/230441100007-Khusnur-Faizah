package com.mycompany.train_system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TrainManagement extends JFrame {
    private JTable trainTable;
    private DefaultTableModel tableModel;
    private JTextField trainIdField, trainNameField, capacityField;
    private JButton createButton, updateButton, deleteButton, refreshButton;

    public TrainManagement(int adminId) {
        setTitle("Train Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set a custom background color
        getContentPane().setBackground(new Color(240, 240, 240));

        // Table model for displaying train data
        String[] columnNames = {"Train ID", "Train Name", "Capacity"};
        tableModel = new DefaultTableModel(columnNames, 0);
        trainTable = new JTable(tableModel);
        trainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allows only one row to be selected
        trainTable.setBackground(Color.WHITE);  // Table background color
        trainTable.setForeground(new Color(0, 102, 204)); // Table text color
        trainTable.setFont(new Font("Arial", Font.PLAIN, 14)); // Table font styling

        // Form Panel Setup
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Train Information"));
        formPanel.setBackground(new Color(255, 255, 255));  // Background color for form panel
        formPanel.setForeground(new Color(50, 50, 50));  // Text color for form panel

        formPanel.add(new JLabel("Train ID:"));
        trainIdField = new JTextField();
        trainIdField.setEditable(false);  // Train ID is not editable
        formPanel.add(trainIdField);

        formPanel.add(new JLabel("Train Name:"));
        trainNameField = new JTextField();
        formPanel.add(trainNameField);

        formPanel.add(new JLabel("Capacity:"));
        capacityField = new JTextField();
        formPanel.add(capacityField);

        // Buttons Panel Setup
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 240, 240));  // Background color for button panel

        createButton = new JButton("Create");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");

        // Add button tooltips for better user experience
        createButton.setToolTipText("Create a new train");
        updateButton.setToolTipText("Update selected train");
        deleteButton.setToolTipText("Delete selected train");
        refreshButton.setToolTipText("Refresh train list");

        // Customizing button colors
        createButton.setBackground(new Color(76, 175, 80)); // Green
        createButton.setForeground(Color.WHITE);
        updateButton.setBackground(new Color(255, 165, 0)); // Orange
        updateButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(255, 69, 58)); // Red
        deleteButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(33, 150, 243)); // Blue
        refreshButton.setForeground(Color.WHITE);

        // Add buttons to panel
        buttonPanel.add(createButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Layout Setup
        setLayout(new BorderLayout(10, 10));
        add(new JScrollPane(trainTable), BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load initial data
        loadTrainData();

        // Button listeners for CRUD operations
        createButton.addActionListener(event -> createTrain());
        updateButton.addActionListener(event -> updateTrain());
        deleteButton.addActionListener(event -> deleteTrain());
        refreshButton.addActionListener(event -> loadTrainData());
    }

    private void loadTrainData() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        tableModel.setRowCount(0); // Clear the table

        try {
            conn = DatabaseConnection.connect();
            String sql = "SELECT train_id, train_name, total_seats FROM trains";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {rs.getInt("train_id"), rs.getString("train_name"), rs.getInt("total_seats")};
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading train data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void createTrain() {
        String trainName = trainNameField.getText();
        String capacity = capacityField.getText();
        if (trainName.isEmpty() || capacity.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.connect();
            String sql = "INSERT INTO trains (train_name, total_seats) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, trainName);
            stmt.setInt(2, Integer.parseInt(capacity));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Train created successfully!");
            loadTrainData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error creating train: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateTrain() {
        int selectedRow = trainTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int trainId = (int) tableModel.getValueAt(selectedRow, 0);
        trainIdField.setText(String.valueOf(trainId)); // Display train ID in the form

        String trainName = trainNameField.getText();
        String capacity = capacityField.getText();

        if (trainName.isEmpty() || capacity.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill out all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.connect();
            String sql = "UPDATE trains SET train_name = ?, total_seats = ? WHERE train_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, trainName);
            stmt.setInt(2, Integer.parseInt(capacity));
            stmt.setInt(3, trainId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Train updated successfully!");
            loadTrainData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating train: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteTrain() {
        int selectedRow = trainTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int trainId = (int) tableModel.getValueAt(selectedRow, 0);

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.connect();
            String sql = "DELETE FROM trains WHERE train_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, trainId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Train deleted successfully!");
            loadTrainData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting train: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
