package Config;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class UIConfig {
    // Minimalist Brown & White Theme Colors
    public static final Color BROWN_PRIMARY = new Color(139, 115, 85); // Professional Muted Brown
    public static final Color BROWN_HOVER = new Color(160, 140, 115);   // Lighter Brown for hover
    public static final Color PURE_WHITE = Color.WHITE;
    public static final Color TABLE_HEADER_BG = new Color(245, 242, 238); // Very light brown/cream
    public static final Color TABLE_SELECTION = new Color(139, 115, 85, 40); // Transparent Brown
    public static final Color TEXT_COLOR = new Color(60, 50, 40); // Dark Brown Text
    
    // Rounded Border Class
    public static class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    public static void styleButton(JButton button) {
        button.setBackground(BROWN_PRIMARY);
        button.setForeground(PURE_WHITE);
        button.setFont(new Font("Century Gothic", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Remove standard borders for a cleaner look
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BROWN_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BROWN_PRIMARY);
            }
        });
    }

    public static void styleTable(JTable table) {
        table.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        table.setRowHeight(35);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(TABLE_SELECTION);
        table.setSelectionForeground(TEXT_COLOR);
        table.setBackground(PURE_WHITE);
        
        // Style Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(PURE_WHITE);
        header.setForeground(TEXT_COLOR);
        header.setFont(new Font("Century Gothic", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BROWN_PRIMARY));
        
        // Center text in cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setBackground(PURE_WHITE);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public static void styleSidebarFeature(JLabel label) {
        label.setFont(new Font("Century Gothic", Font.BOLD, 16));
        label.setForeground(PURE_WHITE);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // "Glowy White" effect
                label.setForeground(new Color(255, 255, 255, 200));
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, PURE_WHITE),
                    BorderFactory.createEmptyBorder(0, 0, 2, 0)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label.setForeground(PURE_WHITE);
                label.setBorder(null);
            }
        });
    }
}
