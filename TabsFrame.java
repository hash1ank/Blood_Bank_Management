import javax.swing.*;
import java.awt.*;

public class TabsFrame extends JFrame {

    public TabsFrame() {
        setTitle("Blood Donation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        setSize(1200, 1200);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Load and scale the background image
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/donate blood save life_blood donation poster (8).jpg"));
        Image bgImage = originalIcon.getImage().getScaledInstance(1200, 1200, Image.SCALE_SMOOTH);

        // Custom background panel
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        // Buttons
        String[] labels = {
            "ENQUIRY", "CLIENT REQUEST", "DONOR", "DONOR HEALTH",
            "BLOOD STOCK", "ISSUE", "EXISTING DONORS", "BACK"
        };

        JButton[] buttons = new JButton[labels.length];
        Font btnFont = new Font("Tahoma", Font.BOLD, 18);
        Color btnColor = new Color(153, 0, 0);

        int x = 100, y = 150, width = 300, height = 50, gap = 80;

        for (int i = 0; i < labels.length; i++) {
            JButton btn = new JButton(labels[i]);
            btn.setFont(btnFont);
            btn.setForeground(btnColor);
            btn.setBounds(x + (i % 2) * (width + 50), y + (i / 2) * gap, width, height);
            buttons[i] = btn;
            bgPanel.add(btn);
        }

        // Action listeners (open corresponding frame)
        buttons[0].addActionListener(e -> navigateTo(new Enquiry()));
        buttons[1].addActionListener(e -> navigateTo(new ClientRequestFrame()));
        buttons[2].addActionListener(e -> navigateTo(new Donor()));
        buttons[3].addActionListener(e -> navigateTo(new Donor_Health()));
        buttons[4].addActionListener(e -> navigateTo(new blood_stock()));
        buttons[5].addActionListener(e -> navigateTo(new ISSUE()));
        buttons[6].addActionListener(e -> navigateTo(new ShowDonorInfoFrame()));
        buttons[7].addActionListener(e -> dispose()); // BACK
    }

    private void navigateTo(JFrame frame) {
        frame.setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TabsFrame().setVisible(true));
    }
}
