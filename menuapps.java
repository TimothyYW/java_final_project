import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class MenuApp extends JFrame {
    private JTextField namaField, hargaField, stokField;
    private JButton insertButton;

    public MenuApp() {
        setTitle("PT Pudding Menu Manager");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));

        Database.createTable(); // create table if not exist

        // Fields
        add(new JLabel("Nama Menu:"));
        namaField = new JTextField();
        add(namaField);

        add(new JLabel("Harga Menu:"));
        hargaField = new JTextField();
        add(hargaField);

        add(new JLabel("Stok Menu:"));
        stokField = new JTextField();
        add(stokField);

        insertButton = new JButton("Insert Menu");
        add(insertButton);

        // Empty panel for layout balance
        add(new JLabel(""));

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertMenu();
            }
        });
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
            String kodeMenu = generateKodeMenu();

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

            // Clear fields after insert
            namaField.setText("");
            hargaField.setText("");
            stokField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga and Stok must be numbers!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
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

