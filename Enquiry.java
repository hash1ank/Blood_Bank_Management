import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Enquiry extends JFrame {
    private JComboBox<String> bgCombo;
    private JComboBox<String> rhCombo;
    private JTable resultTable;
    private JLabel countLabel;
    private JButton searchButton;
    private JButton backButton;

    public Enquiry() {
        setTitle("Enquiry");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBackground(new Color(255,255,153));
        add(mainPanel);

        // Top controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.add(new JLabel("Blood Group:"));
        bgCombo = new JComboBox<>(new String[]{"A", "AB", "B", "O", "BOMBAY BLOOD GROUP"});
        topPanel.add(bgCombo);
        topPanel.add(new JLabel("Rh:"));
        rhCombo = new JComboBox<>(new String[]{"+", "-"});
        topPanel.add(rhCombo);
        searchButton = new JButton("SEARCH");
        topPanel.add(searchButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        bgCombo.addActionListener(e -> rhCombo.setEnabled(!"BOMBAY BLOOD GROUP".equals(bgCombo.getSelectedItem())));

        // Center table
        resultTable = new JTable(new DefaultTableModel(
            new Object[]{"Donor ID", "Bag ID", "Transfusion Date"}, 0));
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(600,300));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.add(new JLabel("No. of Bags Available:"));
        countLabel = new JLabel("0");
        bottomPanel.add(countLabel);
        backButton = new JButton("BACK");
        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        searchButton.addActionListener(e -> performSearch());
        backButton.addActionListener(e -> {
            new TabsFrame().setVisible(true);
            dispose();
        });
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost/blood_donation";
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");
        return DriverManager.getConnection(url, user, pass);
    }

    private void performSearch() {
        String bg = (String) bgCombo.getSelectedItem();
        String rh = (String) rhCombo.getSelectedItem();
        String bloodGroup = "BOMBAY BLOOD GROUP".equals(bg) ? bg : bg + rh;

        DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
        model.setRowCount(0);
        countLabel.setText("0");

        String countSQL = "SELECT COUNT(*) FROM blood_stock WHERE blood_group = ?";
        String detailSQL = "SELECT donor_id, bag_id, transfusion_date FROM blood_stock WHERE blood_group = ?";

        try (Connection con = getConnection();
             PreparedStatement pc = con.prepareStatement(countSQL);
             PreparedStatement pd = con.prepareStatement(detailSQL)) {

            pc.setString(1, bloodGroup);
            try (ResultSet rc = pc.executeQuery()) {
                if (rc.next()) countLabel.setText(String.valueOf(rc.getInt(1)));
            }

            pd.setString(1, bloodGroup);
            try (ResultSet rs = pd.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("donor_id"),
                        rs.getInt("bag_id"),
                        rs.getDate("transfusion_date")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Enquiry().setVisible(true));
    }
}