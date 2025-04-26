import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Random;

public class MenuApp extends JFrame {
    private JTextField namaField, hargaField, stokField;
    private JButton insertButton;
    private JTable menuTable;
    private DefaultTableModel tableModel;

    public MenuApp() {
        setTitle("PT Pudding Menu Manager");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 2, 10, 10)); // Adjust for more buttons

        Database.createTable(); // Ensure database table exists

        // --- Add Labels and TextFields ---
        add(new JLabel("Nama Menu:"));
        namaField = new JTextField();
        add(namaField);

        add(new JLabel("Harga Menu:"));
        hargaField = new JTextField();
        add(hargaField);

        add(new JLabel("Stok Menu:"));
        stokField = new JTextField();
        add(stokField);

        // --- Insert Button ---
        insertButton = new JButton("Insert Menu");
        add(insertButton);

        // --- Update Button ---
        JButton updateButton = new JButton("Update Menu");
        add(updateButton);

        // --- Delete Button ---
        JButton deleteButton = new JButton("Delete Menu");
        add(deleteButton);

        // --- Table to display menus ---
        tableModel = new DefaultTableModel(new String[]{"Kode Menu", "Nama Menu", "Harga Menu", "Stok Menu"}, 0);
        menuTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(menuTable);
        add(scrollPane);

        // Empty label to fix layout
        add(new JLabel(""));

        // --- Insert button action ---
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertMenu();
            }
        });

        // --- Update button action ---
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMenu();
            }
        });

        // --- Delete button action ---
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMenu();
            }
        });

        // --- MouseListener for auto-fill fields when clicking a row ---
        menuTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = menuTable.getSelectedRow();
                if (row >= 0) {
                    namaField.setText(tableModel.getValueAt(row, 1).toString());
                    hargaField.setText(tableModel.getValueAt(row, 2).toString());
                    stokField.setText(tableModel.getValueAt(row, 3).toString());
                }
            }
        });

        // --- Load menu data at startup ---
        loadMenuData();
    }

    private void insertMenu() {
        String nama = namaField.getText();
        String hargaText = hargaField.getText();
        String stokText = stokField.getText();

        if (nama.isEmpty() || hargaText.isEmpty() || stokText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!");
            return;
        }

        try {
            int harga = Integer.parseInt(hargaText);
            int stok = Integer.parseInt(stokText);
            String kodeMenu = generateKodeMenu(); // Random PD-XXX

            String sql = "INSERT INTO menu (kode_menu, nama_menu, harga_menu, stok_menu) VALUES (?, ?, ?, ?)";
            Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, kodeMenu);
            pstmt.setString(2, nama);
            pstmt.setInt(3, harga);
            pstmt.setInt(4, stok);
            pstmt.executeUpdate();
            conn.close();

            JOptionPane.showMessageDialog(this, "Menu inserted successfully with code: " + kodeMenu);

            namaField.setText("");
            hargaField.setText("");
            stokField.setText("");

            loadMenuData(); // Refresh table after insert
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga and Stok must be valid numbers!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void updateMenu() {
        int selectedRow = menuTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a menu item to update!");
            return;
        }

        String kodeMenu = tableModel.getValueAt(selectedRow, 0).toString();
        String newHargaText = hargaField.getText();
        String newStokText = stokField.getText();

        if (newHargaText.isEmpty() || newStokText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harga and Stok fields must not be empty!");
            return;
        }

        try {
            int newHarga = Integer.parseInt(newHargaText);
            int newStok = Integer.parseInt(newStokText);

            String sql = "UPDATE menu SET harga_menu = ?, stok_menu = ? WHERE kode_menu = ?";
            Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newHarga);
            pstmt.setInt(2, newStok);
            pstmt.setString(3, kodeMenu);
            pstmt.executeUpdate();
            conn.close();

            JOptionPane.showMessageDialog(this, "Menu updated successfully!");

            loadMenuData(); // Refresh the table
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga and Stok must be valid numbers!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void deleteMenu() {
        int selectedRow = menuTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a menu item to delete!");
            return;
        }

        String kodeMenu = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this menu?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM menu WHERE kode_menu = ?";
                Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, kodeMenu);
                pstmt.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(this, "Menu deleted successfully!");
                loadMenuData(); // Refresh table after delete
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        }
    }

    private void loadMenuData() {
        tableModel.setRowCount(0); // Clear previous rows

        String sql = "SELECT * FROM menu";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String kode = rs.getString("kode_menu");
                String nama = rs.getString("nama_menu");
                int harga = rs.getInt("harga_menu");
                int stok = rs.getInt("stok_menu");

                tableModel.addRow(new Object[]{kode, nama, harga, stok});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private String generateKodeMenu() {
        Random rand = new Random();
        int randomNum = rand.nextInt(900) + 100; // Random number 100-999
        return "PD-" + randomNum;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuApp().setVisible(true);
        });
    }
}
