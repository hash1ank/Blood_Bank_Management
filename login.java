import javax.swing.*;
import java.awt.*;

public class login extends JFrame {
    private JFormattedTextField tfId;
    private JPasswordField tfPassword;
    private JButton loginButton;

    public login() {
        setTitle("LOGIN");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Main panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        add(panel);

        // Banner
        ImageIcon icon = new ImageIcon(getClass().getResource("/ls.jpg"));
        JLabel banner = new JLabel(icon);
        panel.add(banner, BorderLayout.NORTH);

        // Input fields
        JPanel fields = new JPanel(new GridLayout(2, 2, 10, 10));
        fields.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        fields.add(new JLabel("ENTER USER ID", SwingConstants.RIGHT));
        tfId = new JFormattedTextField();
        fields.add(tfId);
        fields.add(new JLabel("ENTER PASSWORD", SwingConstants.RIGHT));
        tfPassword = new JPasswordField();
        fields.add(tfPassword);
        panel.add(fields, BorderLayout.CENTER);

        // Login button
        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Tempus Sans ITC", Font.BOLD, 48));
        loginButton.setForeground(new Color(153, 0, 153));
        loginButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        loginButton.addActionListener(e -> attemptLogin());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void attemptLogin() {
        int expectedId = 123;
        String expectedPass = "123";
        try {
            int enteredId = Integer.parseInt(tfId.getText().trim());
            String enteredPass = new String(tfPassword.getPassword());
            if (enteredId == expectedId && enteredPass.equals(expectedPass)) {
                TabsFrame tabs = new TabsFrame();
                tabs.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "CHECK YOUR PASSWORD AND ID");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "User ID must be a number.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new login().setVisible(true));
    }
}