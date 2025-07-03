import java.sql.*;
import javax.swing.*;
import java.awt.*;

public class Donor extends JFrame {

    private JTextField tfId, tfName, tfAddress, tfMobile, tfMail;
    private JSpinner spAge, spDate;
    private JComboBox<String> cbOccupation, cbGender, cbBlood, cbRh;
    private JRadioButton rbDiseaseYes, rbDiseaseNo;
    private JButton btnSave, btnReset, btnBack;

    public Donor() {
        setTitle("Donor Information");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(panel);

        // Input section
        JPanel input = new JPanel(new GridLayout(8, 2, 10, 10));
        input.add(new JLabel("Donor ID:")); tfId = new JTextField(); input.add(tfId);
        input.add(new JLabel("Name:")); tfName = new JTextField(); input.add(tfName);
        input.add(new JLabel("Address:")); tfAddress = new JTextField(); input.add(tfAddress);
        input.add(new JLabel("Mobile:")); tfMobile = new JTextField(); input.add(tfMobile);
        input.add(new JLabel("Email:")); tfMail = new JTextField(); input.add(tfMail);
        input.add(new JLabel("Age:")); spAge = new JSpinner(new SpinnerNumberModel(18, 18, 100, 1)); input.add(spAge);
        input.add(new JLabel("Occupation:")); cbOccupation = new JComboBox<>(new String[]{"GOVT. SERVICE", "PRIVATE JOB", "SELF EMPLOYED", "STUDENT", "OTHERS"}); input.add(cbOccupation);
        input.add(new JLabel("Gender:")); cbGender = new JComboBox<>(new String[]{"MALE", "FEMALE"}); input.add(cbGender);
        panel.add(input, BorderLayout.CENTER);

        // Extras
        JPanel extras = new JPanel(new GridLayout(4, 2, 10, 10));
        extras.add(new JLabel("Blood Group:"));
        cbBlood = new JComboBox<>(new String[]{"A", "B", "AB", "O", "BOMBAY BLOOD GROUP"}); extras.add(cbBlood);

        extras.add(new JLabel("Rh:"));
        cbRh = new JComboBox<>(new String[]{"POSITIVE", "NEGATIVE"}); extras.add(cbRh);

        extras.add(new JLabel("Any Disease:"));
        JPanel diseasePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbDiseaseYes = new JRadioButton("Yes");
        rbDiseaseNo = new JRadioButton("No", true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbDiseaseYes);
        bg.add(rbDiseaseNo);
        diseasePanel.add(rbDiseaseYes);
        diseasePanel.add(rbDiseaseNo);
        extras.add(diseasePanel);

        extras.add(new JLabel("Donation Date:"));
        spDate = new JSpinner(new SpinnerDateModel());
        spDate.setEditor(new JSpinner.DateEditor(spDate, "yyyy-MM-dd"));
        extras.add(spDate);

        panel.add(extras, BorderLayout.EAST);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnReset = new JButton("Reset"); btnReset.addActionListener(e -> clearFields()); buttons.add(btnReset);
        btnSave = new JButton("Save Data"); btnSave.addActionListener(e -> saveDonor()); buttons.add(btnSave);
        btnBack = new JButton("Back"); btnBack.addActionListener(e -> { new TabsFrame().setVisible(true); dispose(); }); buttons.add(btnBack);
        panel.add(buttons, BorderLayout.SOUTH);

        // Logic
        cbBlood.addActionListener(e -> cbRh.setEnabled(!"BOMBAY BLOOD GROUP".equals(cbBlood.getSelectedItem())));
        rbDiseaseYes.addActionListener(e -> btnSave.setEnabled(false));
        rbDiseaseNo.addActionListener(e -> btnSave.setEnabled(true));
    }

    private void clearFields() {
        tfId.setText(""); tfName.setText(""); tfAddress.setText("");
        tfMobile.setText(""); tfMail.setText("");
        spAge.setValue(18); cbOccupation.setSelectedIndex(0);
        cbGender.setSelectedIndex(0); cbBlood.setSelectedIndex(0); cbRh.setSelectedIndex(0);
        rbDiseaseNo.setSelected(true);
        spDate.setValue(new java.util.Date());
    }

    private void saveDonor() {
        if (tfId.getText().trim().isEmpty() || tfName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return;
        }

        try {
            int donorId = Integer.parseInt(tfId.getText().trim());
            int age = (int) spAge.getValue();
            String name = tfName.getText().trim();
            String address = tfAddress.getText().trim();
            String mobile = tfMobile.getText().trim();
            String email = tfMail.getText().trim();
            String occupation = (String) cbOccupation.getSelectedItem();
            String gender = (String) cbGender.getSelectedItem();
            String bg = (String) cbBlood.getSelectedItem();
            String rh = (String) cbRh.getSelectedItem();
            String sign = "POSITIVE".equals(rh) ? "+" : "-";
            String bloodGroup = "BOMBAY BLOOD GROUP".equals(bg) ? bg : bg + sign;
            String disease = rbDiseaseYes.isSelected() ? "yes" : "no";

            // Convert util.Date to sql.Date
            java.util.Date utilDate = (java.util.Date) spDate.getValue();
            java.sql.Date donatedDate = new java.sql.Date(utilDate.getTime());

            String url = "jdbc:mysql://localhost/blood_donation";
            String user = System.getenv("DB_USER");
            String pass = System.getenv("DB_PASS");

            String donorSQL = "INSERT INTO donor (donor_id, name, age, address, gender, mobile, email, occupation, blood_group, disease_history, donation_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String stockSQL = "INSERT INTO blood_stock (donor_id, bag_id, transfusion_date, blood_group) VALUES (?, ?, ?, ?)";

            try (Connection con = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pd = con.prepareStatement(donorSQL);
                 PreparedStatement psCount = con.prepareStatement("SELECT COALESCE(MAX(bag_id),0)+1 FROM blood_stock");
                 ResultSet rs = psCount.executeQuery()) {

                int bagId = 1;
                if (rs.next()) bagId = rs.getInt(1);

                // Insert donor
                pd.setInt(1, donorId);
                pd.setString(2, name);
                pd.setInt(3, age);
                pd.setString(4, address);
                pd.setString(5, gender);
                pd.setString(6, mobile);
                pd.setString(7, email);
                pd.setString(8, occupation);
                pd.setString(9, bloodGroup);
                pd.setString(10, disease);
                pd.setDate(11, donatedDate);
                pd.executeUpdate();

                // Insert into stock
                try (PreparedStatement psStock = con.prepareStatement(stockSQL)) {
                    psStock.setInt(1, donorId);
                    psStock.setInt(2, bagId);
                    psStock.setDate(3, donatedDate);
                    psStock.setString(4, bloodGroup);
                    psStock.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Data saved. Thank you.");
                clearFields();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID and age must be numbers.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Donor().setVisible(true));
    }
}
