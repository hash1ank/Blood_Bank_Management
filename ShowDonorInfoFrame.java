import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ShowDonorInfoFrame extends JFrame {
    private JTable donorTable;
    private JButton showInfoButton;
    private JLabel countLabel;

    public ShowDonorInfoFrame() {
        setTitle("Existing Donors");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(255, 255, 153));

        // Header label
        JLabel header = new JLabel("EXISTING DONORS", SwingConstants.CENTER);
        header.setFont(new Font("Wide Latin", Font.BOLD, 48));
        header.setForeground(new Color(0, 153, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        // Table setup
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"DONOR_ID", "DONOR_NAME", "BLOOD_GROUP", "MOBILE_NUMBER"}, 0);
        donorTable = new JTable(model);
        donorTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(donorTable);
        scrollPane.setPreferredSize(new Dimension(800, 200));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Controls
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        showInfoButton = new JButton("SHOW INFO");
        showInfoButton.setFont(new Font("Tahoma", Font.BOLD, 24));
        showInfoButton.setForeground(Color.RED);
        showInfoButton.addActionListener(e -> loadDonorInfo());

        bottomPanel.add(showInfoButton);

        bottomPanel.add(new JLabel("No. of Donors:"));
        countLabel = new JLabel("0");
        countLabel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        countLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
        bottomPanel.add(countLabel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost/blood_donation";
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");
        return DriverManager.getConnection(url, user, pass);
    }

    private void loadDonorInfo() {
        DefaultTableModel dm = (DefaultTableModel) donorTable.getModel();
        dm.setRowCount(0);
        String sql = "SELECT donor_id, donor_name, blood_group, mobile_number FROM donor";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int count = 0;
            while (rs.next()) {
                count++;
                dm.addRow(new Object[]{
                    rs.getInt("donor_id"),
                    rs.getString("donor_name"),
                    rs.getString("blood_group"),
                    rs.getString("mobile_number")
                });
            }
            countLabel.setText(String.valueOf(count));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading donors: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShowDonorInfoFrame().setVisible(true));
    }
}
