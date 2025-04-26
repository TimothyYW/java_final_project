import javax.swing.*;
import java.awt.*;

public class MenuApp extends JFrame {

    public MenuApp() {
        setTitle("PT Pudding Menu Manager");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout());

        Database.createTable(); // make sure table exists

        JLabel welcomeLabel = new JLabel("Welcome to PT Pudding Menu Manager", SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuApp().setVisible(true);
        });
    }
}
