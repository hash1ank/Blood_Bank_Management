import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.*;

public class ClientRequestFrame extends JFrame {
    private String BG;

    public ClientRequestFrame() {
        setTitle("Client Request");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width, screenSize.height);
    }

    private void initComponents() {
    setLayout(null);

    JLabel lblTitle = new JLabel("Client Request Form");
    lblTitle.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 28));
    lblTitle.setBounds(450, 20, 400, 40);
    add(lblTitle);

    // Patient ID
    addLabel("Patient ID:", 100, 100);
    tfP_id = new JTextField();
    tfP_id.setBounds(250, 100, 200, 25);
    add(tfP_id);

    addLabel("Name:", 100, 140);
    tfP_name = new JTextField();
    tfP_name.setBounds(250, 140, 200, 25);
    add(tfP_name);

    addLabel("Age:", 100, 180);
    tfP_age = new JSpinner(new SpinnerNumberModel(18, 0, 120, 1));
    tfP_age.setBounds(250, 180, 200, 25);
    add(tfP_age);

    addLabel("Gender:", 100, 220);
    jcmbgender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
    jcmbgender.setBounds(250, 220, 200, 25);
    add(jcmbgender);

    addLabel("Weight (kg):", 100, 260);
    tfP_weight = new JTextField();
    tfP_weight.setBounds(250, 260, 200, 25);
    add(tfP_weight);

    addLabel("Address:", 100, 300);
    tfP_address = new JTextArea();
    JScrollPane spAddress = new JScrollPane(tfP_address);
    spAddress.setBounds(250, 300, 200, 60);
    add(spAddress);

    addLabel("Guardian Name:", 500, 100);
    tfP_father_husband_name = new JFormattedTextField();
    tfP_father_husband_name.setBounds(700, 100, 200, 25);
    add(tfP_father_husband_name);

    addLabel("Hospital Name:", 500, 140);
    tfP_hospital_name = new JFormattedTextField();
    tfP_hospital_name.setBounds(700, 140, 200, 25);
    add(tfP_hospital_name);

    addLabel("Doctor In-charge:", 500, 180);
    tfP_doc_incharge = new JFormattedTextField();
    tfP_doc_incharge.setBounds(700, 180, 200, 25);
    add(tfP_doc_incharge);

    addLabel("Disease:", 500, 220);
    tfP_disease = new JFormattedTextField();
    tfP_disease.setBounds(700, 220, 200, 25);
    add(tfP_disease);

    addLabel("Disease History:", 500, 260);
    tfP_disease_history = new JFormattedTextField();
    tfP_disease_history.setBounds(700, 260, 200, 25);
    add(tfP_disease_history);

    addLabel("Mobile No:", 500, 300);
    tfP_mobile = new JFormattedTextField();
    tfP_mobile.setBounds(700, 300, 200, 25);
    add(tfP_mobile);

    addLabel("Blood Group:", 100, 380);
    jcmbblood = new JComboBox<>(new String[]{"A", "B", "AB", "O", "BOMBAY BLOOD GROUP"});
    jcmbblood.setBounds(250, 380, 200, 25);
    add(jcmbblood);

    addLabel("Rh Factor:", 500, 380);
    jcmbRh = new JComboBox<>(new String[]{"POSITIVE", "NEGATIVE"});
    jcmbRh.setBounds(700, 380, 200, 25);
    add(jcmbRh);

    addLabel("Units Required:", 100, 420);
    jreqblood = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
    jreqblood.setBounds(250, 420, 200, 25);
    add(jreqblood);

    // Buttons
    JButton btnSave = new JButton("Save");
    JButton btnClear = new JButton("Clear");
    JButton btnBack = new JButton("Back");

    btnSave.setBounds(300, 480, 100, 30);
    btnClear.setBounds(420, 480, 100, 30);
    btnBack.setBounds(540, 480, 100, 30);

    add(btnSave);
    add(btnClear);
    add(btnBack);

    btnSave.addActionListener(e -> handleSaveRequest());
    btnClear.addActionListener(e -> clearFields());
    btnBack.addActionListener(e -> navigateBack());

    // Optional: Enable/disable Rh dropdown
    jcmbblood.addActionListener(e -> {
        String bg = (String) jcmbblood.getSelectedItem();
        jcmbRh.setEnabled(!"BOMBAY BLOOD GROUP".equals(bg));
    });
}

// Utility method for adding labels
private void addLabel(String text, int x, int y) {
    JLabel label = new JLabel(text);
    label.setBounds(x, y, 130, 25);
    add(label);
}


    private void clearFields() {
        tfP_id.setText("");
        tfP_name.setText("");
        tfP_address.setText("");
        tfP_disease.setText("");
        tfP_doc_incharge.setText("");
        tfP_disease_history.setText("");
        tfP_hospital_name.setText("");
        tfP_father_husband_name.setText("");
        tfP_mobile.setText("");
        tfP_weight.setText("");
        jreqblood.setValue(1);
        tfP_age.setValue(18);
        jcmbblood.setSelectedIndex(0);
        jcmbRh.setSelectedIndex(0);
        jcmbgender.setSelectedIndex(0);
    }

    private void handleSaveRequest() {
        if (tfP_id.getText().trim().isEmpty() || tfP_name.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return;
        }
        int P_id;
        float weight;
        try {
            P_id = Integer.parseInt(tfP_id.getText().trim());
            weight = Float.parseFloat(tfP_weight.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Patient ID must be integer and Weight must be a valid number.");
            return;
        }

        int age = (int) tfP_age.getValue();
        String name = tfP_name.getText().trim();
        String address = tfP_address.getText().trim();
        String father_husband_name = tfP_father_husband_name.getText().trim();
        String hospital = tfP_hospital_name.getText().trim();
        String doctor = tfP_doc_incharge.getText().trim();
        String disease = tfP_disease.getText().trim();
        String disease_history = tfP_disease_history.getText().trim();
        String bloodQty = jreqblood.getValue().toString();
        String mobile = tfP_mobile.getText().trim();
        String gender = (String) jcmbgender.getSelectedItem();
        String bg = (String) jcmbblood.getSelectedItem();
        String rh = (String) jcmbRh.getSelectedItem();

        String sign = "POSITIVE".equals(rh) ? "+" : "NEGATIVE".equals(rh) ? "-" : "";
        BG = "BOMBAY BLOOD GROUP".equals(bg) ? bg : bg + sign;

        String url = "jdbc:mysql://localhost/blood_donation";
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");

        String insertSQL = "INSERT INTO client_request (patient_id, name, age, address, gender, weight, guardian_name, hospital_name, doctor_incharge, blood_group, disease, disease_history, mobile, quantity) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = con.prepareStatement(insertSQL)) {

            pstmt.setInt(1, P_id);
            pstmt.setString(2, name);
            pstmt.setInt(3, age);
            pstmt.setString(4, address);
            pstmt.setString(5, gender);
            pstmt.setFloat(6, weight);
            pstmt.setString(7, father_husband_name);
            pstmt.setString(8, hospital);
            pstmt.setString(9, doctor);
            pstmt.setString(10, BG);
            pstmt.setString(11, disease);
            pstmt.setString(12, disease_history);
            pstmt.setString(13, mobile);
            pstmt.setString(14, bloodQty);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Your data is saved! Thank you.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void navigateBack() {
        new TabsFrame().setVisible(true);
        dispose();
    }

    // UI Components (declare them as per your design)
    private JTextField tfP_id, tfP_name, tfP_weight;
    private JTextArea tfP_address;
    private JFormattedTextField tfP_disease, tfP_doc_incharge, tfP_disease_history, tfP_hospital_name, tfP_father_husband_name, tfP_mobile;
    private JSpinner tfP_age, jreqblood;
    private JComboBox<String> jcmbblood, jcmbRh, jcmbgender;
    // Add your buttons and link them to methods above
}
