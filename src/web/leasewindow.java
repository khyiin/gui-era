package web;

import Config.config;
import Config.UIConfig;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class leasewindow extends JFrame {

    private final JTextArea leaseText;

    public leasewindow(String reservationId) {
        setTitle("Lease Agreement - " + reservationId);
        setSize(700, 850); // Bond paper aspect ratio roughly
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main content area - white background like paper
        leaseText = new JTextArea();
        leaseText.setEditable(false);
        leaseText.setFont(new Font("Serif", Font.PLAIN, 14)); // Serif font for formal look
        leaseText.setBackground(Color.WHITE);
        leaseText.setForeground(Color.BLACK);
        leaseText.setMargin(new Insets(50, 50, 50, 50)); // Large margins like real paper
        leaseText.setLineWrap(true);
        leaseText.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(leaseText);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scrollPane.setBackground(new Color(240, 240, 240)); // Gray background for the scrollpane area

        JButton printButton = new JButton("Print Agreement");
        UIConfig.styleBrownButton(printButton); // Use our brown theme for the button
        printButton.addActionListener(e -> printLease());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        bottomPanel.add(printButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));
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
                    String today = new SimpleDateFormat("MMMM dd, yyyy").format(new Date());
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append("\t\t\tRESIDENTIAL LEASE AGREEMENT\n");
                    sb.append("\t\t\t---------------------------\n\n");
                    
                    sb.append("DATE: ").append(today).append("\n");
                    sb.append("RESERVATION ID: ").append(rs.getString("res_id")).append("\n");
                    sb.append("STATUS: ").append(rs.getString("status").toUpperCase()).append("\n\n");

                    sb.append("1. PARTIES\n");
                    sb.append("This Lease Agreement is made between:\n");
                    sb.append("LANDLORD: ").append(rs.getString("landlord_name")).append("\n");
                    sb.append("TENANT:   ").append(rs.getString("tenant_name")).append("\n");
                    sb.append("CONTACT:  ").append(rs.getString("contact")).append("\n\n");

                    sb.append("2. PROPERTY\n");
                    sb.append("The Landlord agrees to lease the following property to the Tenant:\n");
                    sb.append("ROOM NAME: ").append(rs.getString("r_name")).append("\n");
                    sb.append("LOCATION:  ").append(rs.getString("r_location")).append("\n\n");

                    sb.append("3. TERM AND RENT\n");
                    sb.append("The lease term shall begin on ").append(rs.getString("move_in_date")).append(".\n");
                    sb.append("The agreed contract duration is: ").append(rs.getString("contract")).append(".\n");
                    sb.append("The monthly rental amount is set at: PHP ").append(rs.getString("r_price")).append(".\n\n");

                    sb.append("4. TERMS AND CONDITIONS\n");
                    sb.append("a) The Tenant shall maintain the property in good condition.\n");
                    sb.append("b) Rent must be paid on or before the due date specified by the Landlord.\n");
                    sb.append("c) Any damages to the property will be the responsibility of the Tenant.\n");
                    sb.append("d) The Landlord reserves the right to inspect the property with prior notice.\n\n");

                    sb.append("\n\n\n");
                    sb.append("__________________________\t\t\t__________________________\n");
                    sb.append("      Landlord Signature  \t\t\t      Tenant Signature    \n");

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

