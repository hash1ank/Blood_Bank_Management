import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ISSUE extends JFrame {
    private JComboBox<Integer> cbBagId;
    private JComboBox<Integer> cbPatientId;
    private JTable tablePatients;
    private JTable tableIssues;
    private JButton btnFetchBags;
    private JButton btnFetchPatients;
    private JButton btnFetchAllPatients;
    private JButton btnIssue;
    private JButton btnBack;

    public ISSUE() {
        setTitle("ISSUE RECORD");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Top controls
        JPanel controls = new JPanel(new GridLayout(2, 3, 10, 10));
        cbBagId = new JComboBox<>();
        cbPatientId = new JComboBox<>();
        btnFetchBags = new JButton("Fetch Bag IDs");
        btnFetchPatients = new JButton("Fetch Patient IDs");
        btnFetchAllPatients = new JButton("Fetch All Patients");
        btnIssue = new JButton("Issue Blood");

        controls.add(new JLabel("Select Bag ID:"));
        controls.add(cbBagId);
        controls.add(btnFetchBags);
        controls.add(new JLabel("Select Patient ID:"));
        controls.add(cbPatientId);
        controls.add(btnFetchPatients);

        panel.add(controls, BorderLayout.NORTH);

        // Center tables
        tablePatients = new JTable(new DefaultTableModel(
            new Object[]{"S.No", "Patient ID", "Name", "Age", "Blood Group"}, 0));
        tableIssues = new JTable(new DefaultTableModel(
            new Object[]{"S.No", "Patient ID", "Name", "Bag ID", "Issue Date"}, 0));
        JPanel centerPanel = new JPanel(new GridLayout(2,1,10,10));
        centerPanel.add(new JScrollPane(tablePatients));
        centerPanel.add(new JScrollPane(tableIssues));
        panel.add(centerPanel, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnFetchAllPatients);
        bottom.add(btnIssue);
        btnBack = new JButton("Back");
        bottom.add(btnBack);
        panel.add(bottom, BorderLayout.SOUTH);

        add(panel);

        // Action listeners
        btnFetchBags.addActionListener(e -> fetchBagIds());
        btnFetchPatients.addActionListener(e -> fetchPatientIds());
        btnFetchAllPatients.addActionListener(e -> populatePatientTable());
        btnIssue.addActionListener(e -> issueBlood());
        btnBack.addActionListener(e -> backToTabs());
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost/blood_donation";
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");
        return DriverManager.getConnection(url, user, pass);
    }

    private void fetchBagIds() {
        cbBagId.removeAllItems();
        String sql = "SELECT Bag_ID FROM blood_stock ORDER BY Bag_ID";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cbBagId.addItem(rs.getInt("Bag_ID"));
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void fetchPatientIds() {
        cbPatientId.removeAllItems();
        String sql = "SELECT Patient_ID FROM client_request ORDER BY Patient_ID";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cbPatientId.addItem(rs.getInt("Patient_ID"));
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void populatePatientTable() {
        DefaultTableModel dm = (DefaultTableModel) tablePatients.getModel();
        dm.setRowCount(0);
        String sql = "SELECT Patient_ID, Patient_Name, Age, Blood_Group FROM client_request";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int count = 1;
            while (rs.next()) {
                dm.addRow(new Object[]{count++, rs.getInt("Patient_ID"),
                    rs.getString("Patient_Name"), rs.getInt("Age"), rs.getString("Blood_Group")});
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void issueBlood() {
        Integer bagId = (Integer) cbBagId.getSelectedItem();
        Integer patientId = (Integer) cbPatientId.getSelectedItem();
        if (bagId == null || patientId == null) {
            JOptionPane.showMessageDialog(this, "Please select both Bag ID and Patient ID.");
            return;
        }
        String nameSql = "SELECT Patient_Name FROM client_request WHERE Patient_ID = ?";
        String issueSql = "INSERT INTO issue (bag_id, patient_id, issue_datetime) VALUES (?,?,?)";
        String deleteSql = "DELETE FROM blood_stock WHERE Bag_ID = ?";
        try (Connection con = getConnection();
             PreparedStatement psName = con.prepareStatement(nameSql);
             PreparedStatement psIssue = con.prepareStatement(issueSql);
             PreparedStatement psDelete = con.prepareStatement(deleteSql)) {

            // Fetch name
            psName.setInt(1, patientId);
            try (ResultSet rs = psName.executeQuery()) {
                if (rs.next()) {
                    String patientName = rs.getString("Patient_Name");

                    // Insert issue record
                    psIssue.setInt(1, bagId);
                    psIssue.setInt(2, patientId);
                    psIssue.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    psIssue.executeUpdate();

                    // Remove from stock
                    psDelete.setInt(1, bagId);
                    psDelete.executeUpdate();

                    // Update issues table
                    DefaultTableModel dm = (DefaultTableModel) tableIssues.getModel();
                    int next = dm.getRowCount() + 1;
                    dm.addRow(new Object[]{next, patientId, patientName, bagId,
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())});

                    JOptionPane.showMessageDialog(this, "Blood issued successfully.");
                }
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void backToTabs() {
        TabsFrame t = new TabsFrame();
        t.setVisible(true);
        dispose();
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ISSUE().setVisible(true));
    }
}
