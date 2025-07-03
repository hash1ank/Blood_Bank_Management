import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.*;
import javax.swing.*;

public class Donor_Health extends JFrame {
    private JTextField tfDonorId, tfWeight, tfHeight, tfHemoglobin, tfBPStatus;
    private JComboBox<String> cbGender;
    private JButton btnReset, btnSave, btnBack;

    public Donor_Health() {
        setTitle("Donor Health Information");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel fields = new JPanel(new GridLayout(6, 2, 10, 10));
        fields.add(new JLabel("Donor ID:")); tfDonorId = new JTextField(); fields.add(tfDonorId);
        fields.add(new JLabel("Gender:")); cbGender = new JComboBox<>(new String[]{"MALE", "FEMALE"}); fields.add(cbGender);
        fields.add(new JLabel("Weight (kg):")); tfWeight = new JTextField(); fields.add(tfWeight);
        fields.add(new JLabel("Height (cm):")); tfHeight = new JTextField(); fields.add(tfHeight);
        fields.add(new JLabel("Hemoglobin (g/dL):")); tfHemoglobin = new JTextField(); fields.add(tfHemoglobin);
        fields.add(new JLabel("Blood Pressure Status:")); tfBPStatus = new JTextField(); fields.add(tfBPStatus);
        panel.add(fields, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnReset = new JButton("Reset");
        btnSave = new JButton("Save");
        btnBack = new JButton("Back");

        btnReset.addActionListener(e -> resetFields());
        btnBack.addActionListener(e -> { new TabsFrame().setVisible(true); dispose(); });
        btnSave.addActionListener(e -> saveHealthData());

        buttons.add(btnReset);
        buttons.add(btnSave);
        buttons.add(btnBack);
        panel.add(buttons, BorderLayout.SOUTH);

        add(panel);
    }

    private void resetFields() {
        tfDonorId.setText("");
        cbGender.setSelectedIndex(0);
        tfWeight.setText("");
        tfHeight.setText("");
        tfHemoglobin.setText("");
        tfBPStatus.setText("");
    }

    private void saveHealthData() {
        String idText = tfDonorId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Donor ID.");
            return;
        }
        try {
            int donorId = Integer.parseInt(idText);
            float weight = Float.parseFloat(tfWeight.getText().trim());
            float height = Float.parseFloat(tfHeight.getText().trim());
            float hemoglobin = Float.parseFloat(tfHemoglobin.getText().trim());
            String bpStatus = tfBPStatus.getText().trim();
            String gender = (String) cbGender.getSelectedItem();

            String url = "jdbc:mysql://localhost/blood_donation";
            String user = System.getenv("DB_USER");
            String pass = System.getenv("DB_PASS");
            String insertSQL = "INSERT INTO donor_health (donor_id, gender, weight, height, haemoglobin, bp_status) VALUES (?,?,?,?,?,?)";

            try (Connection con = DriverManager.getConnection(url, user, pass);
                 PreparedStatement ps = con.prepareStatement(insertSQL)) {
                ps.setInt(1, donorId);
                ps.setString(2, gender);
                ps.setFloat(3, weight);
                ps.setFloat(4, height);
                ps.setFloat(5, hemoglobin);
                ps.setString(6, bpStatus);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Your data is saved! Thank you.");
                resetFields();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be an integer and other fields must be numbers.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Donor_Health().setVisible(true));
    }
}