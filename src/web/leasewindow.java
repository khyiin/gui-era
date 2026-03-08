package web;

import Config.config;
import java.awt.BorderLayout;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class leasewindow extends JFrame {

    private final JTextArea leaseText;

    public leasewindow(String reservationId) {
        setTitle("Lease Details");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        leaseText = new JTextArea();
        leaseText.setEditable(false);
        leaseText.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(leaseText);

        JButton printButton = new JButton("Print");
        printButton.addActionListener(e -> printLease());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(printButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        loadLeaseData(reservationId);
    }

    private void loadLeaseData(String reservationId) {
        String sql =
            "SELECT res.res_id, res.contact, res.move_in_date, res.contract, res.status, " +
            "       r.r_name, r.r_location, r.r_price, " +
            "       t.first_name || ' ' || t.last_name AS tenant_name, " +
            "       l.first_name || ' ' || l.last_name AS landlord_name " +
            "FROM reservations res " +
            "JOIN rooms r ON res.r_id = r.r_id " +
            "JOIN users t ON res.id = t.id " +
            "JOIN users l ON r.id = l.id " +
            "WHERE res.res_id = ?";

        try (Connection conn = config.connectDB();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, reservationId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("LEASE AGREEMENT\n");
                    sb.append("Reservation ID: ").append(rs.getString("res_id")).append("\n");
                    sb.append("Status: ").append(rs.getString("status")).append("\n\n");

                    sb.append("Landlord: ").append(rs.getString("landlord_name")).append("\n");
                    sb.append("Tenant : ").append(rs.getString("tenant_name")).append("\n");
                    sb.append("Contact: ").append(rs.getString("contact")).append("\n\n");

                    sb.append("Room Name : ").append(rs.getString("r_name")).append("\n");
                    sb.append("Location  : ").append(rs.getString("r_location")).append("\n");
                    sb.append("Price     : ").append(rs.getString("r_price")).append("\n\n");

                    sb.append("Move-in Date : ").append(rs.getString("move_in_date")).append("\n");
                    sb.append("Contract     : ").append(rs.getString("contract")).append("\n\n");

                    sb.append("By proceeding, both parties agree to the terms of this lease.");

                    leaseText.setText(sb.toString());
                    leaseText.setCaretPosition(0);
                } else {
                    leaseText.setText("No lease data found for reservation " + reservationId);
                }
            }
        } catch (SQLException e) {
            leaseText.setText("Error loading lease data: " + e.getMessage());
        }
    }

    private void printLease() {
        try {
            boolean done = leaseText.print();
            if (!done) {
                JOptionPane.showMessageDialog(this, "Printing was cancelled.");
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Failed to print lease: " + e.getMessage());
        }
    }
}

