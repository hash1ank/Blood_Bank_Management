import javax.swing.*;
import java.awt.*;

public class home_page extends JFrame {
    private JButton getStartedButton;
    private JLabel bannerLabel;

    public home_page() {
        setTitle("HOME PAGE");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Use BorderLayout for simplicity
        setLayout(new BorderLayout());

        // Banner image at top
        ImageIcon icon = new ImageIcon(getClass().getResource("/sb.jpg"));
        bannerLabel = new JLabel(icon);
        add(bannerLabel, BorderLayout.NORTH);

        // Button panel in center
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        getStartedButton = new JButton("GET STARTED");
        getStartedButton.setFont(new Font("Tempus Sans ITC", Font.BOLD, 48));
        getStartedButton.setForeground(new Color(153, 0, 153));
        getStartedButton.setBorder(BorderFactory.createLineBorder(new Color(51, 0, 51), 2));
        getStartedButton.addActionListener(e -> openLogin());
        buttonPanel.add(getStartedButton);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private void openLogin() {
        login l = new login();
        l.setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new home_page().setVisible(true);
        });
    }
}
