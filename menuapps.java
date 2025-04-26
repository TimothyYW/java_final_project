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
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));

        Database.createTable(); // Make sure table exists

        // Add Labels and Fields
        add(new JLabel("Nama Menu:"));
        namaField = new JTextField();
        add(namaField);

        add(new JLabel("Harga Menu:"));
        hargaField = new JTextField();
        add(hargaField);

        add(new JLabel("Stok Menu:"));
        stokField = new JTextField();
        add(stokField);

        // Insert Button
        insertButton = new JButton("Insert Menu");
        add(insertButton);

        // Empty label to fix layout
        add(new JLabel(""));

        // Table to show the menus
        tableModel = new DefaultTableModel(new String[]{"Kode Menu", "Nama Menu", "Harga Menu", "Stok Menu"}, 0);
        menuTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(menuTable);
        add(scrollPane);

        // Empty label to fix layout
        add(new JLabel(""));

        // Insert Button Action
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertMenu();
            }
        });

        loadMenuData(); // Load data initially
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
            String kodeMenu = generateKodeMenu(); // Random kode: PD-XXX

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

            loadMenuData(); 
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga and Stok must be valid numbers!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void loadMenuData() {
        tableModel.setRowCount(0); 
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
        int randomNum = rand.nextInt(900) + 100; 
        return "PD-" + randomNum;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuApp().setVisible(true);
        });
    }
}
