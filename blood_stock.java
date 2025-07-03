import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.*;

public class blood_stock extends JFrame {
    public blood_stock() {
        setTitle("Blood Stock Entry");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width, screenSize.height);
    }

    private void initComponents() {
    setLayout(null); // Using absolute positioning

    JLabel lblTitle = new JLabel("Blood Stock Entry");
    lblTitle.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 28));
    lblTitle.setBounds(450, 30, 400, 40);
    add(lblTitle);

    JLabel lblDonorId = new JLabel("Donor ID:");
    lblDonorId.setBounds(200, 120, 100, 25);
    add(lblDonorId);

    tf_did = new JTextField();
    tf_did.setBounds(300, 120, 200, 25);
    add(tf_did);

    JLabel lblBagId = new JLabel("Bag ID:");
    lblBagId.setBounds(200, 160, 100, 25);
    add(lblBagId);

    tf_bagid = new JTextField();
    tf_bagid.setBounds(300, 160, 200, 25);
    add(tf_bagid);

    JLabel lblBG = new JLabel("Blood Group:");
    lblBG.setBounds(200, 200, 100, 25);
    add(lblBG);

    cb_bg = new JComboBox<>(new String[] {
        "A", "B", "AB", "O", "BOMBAY BLOOD GROUP"
    });
    cb_bg.setBounds(300, 200, 200, 25);
    add(cb_bg);

    JLabel lblRh = new JLabel("Rh Factor:");
    lblRh.setBounds(200, 240, 100, 25);
    add(lblRh);

    cb_rh = new JComboBox<>(new String[] { "+", "-" });
    cb_rh.setBounds(300, 240, 200, 25);
    add(cb_rh);

    JLabel lblDate = new JLabel("Transfusion Date:");
    lblDate.setBounds(200, 280, 150, 25);
    add(lblDate);

    t_date = new JSpinner(new SpinnerDateModel());
    t_date.setEditor(new JSpinner.DateEditor(t_date, "yyyy-MM-dd"));
    t_date.setBounds(350, 280, 150, 25);
    add(t_date);

    // Buttons
    jButton1 = new JButton("Save");
    jButton2 = new JButton("Clear");
    jButton3 = new JButton("Back");
    jButton4 = new JButton("Exit");

    jButton1.setBounds(200, 330, 100, 30);
    jButton2.setBounds(320, 330, 100, 30);
    jButton3.setBounds(440, 330, 100, 30);
    jButton4.setBounds(560, 330, 100, 30);

    add(jButton1);
    add(jButton2);
    add(jButton3);
    add(jButton4);

    // Event bindings
    jButton1.addActionListener(e -> saveBloodStock());
    jButton2.addActionListener(e -> clearFields());
    jButton3.addActionListener(e -> navigateBack());
    jButton4.addActionListener(e -> exitApplication());
    cb_bg.addActionListener(e -> handleBloodGroupChange());
}


    private void clearFields() {
        tf_did.setText("");
        tf_bagid.setText("");
    }

    private void saveBloodStock() {
        String didText = tf_did.getText().trim();
        String bagText = tf_bagid.getText().trim();
        if (didText.isEmpty() || bagText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return;
        }

        int donor_id, bag_id;
        try {
            donor_id = Integer.parseInt(didText);
            bag_id = Integer.parseInt(bagText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Donor ID and Bag ID must be valid numbers.");
            return;
        }

        String bg = (String) cb_bg.getSelectedItem();
        String rh = (String) cb_rh.getSelectedItem();
        String bloodGroup = bg.equals("BOMBAY BLOOD GROUP") ? bg : bg + rh;

        java.util.Date utilDate = (java.util.Date) t_date.getValue();
        Date sqlDate = new Date(utilDate.getTime());

        String url = "jdbc:mysql://localhost/blood_donation";
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");

        String insertSQL = "INSERT INTO Blood_STOCK (donor_id, bag_id, transfusion_date, blood_group) VALUES (?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = con.prepareStatement(insertSQL)) {

            pstmt.setInt(1, donor_id);
            pstmt.setInt(2, bag_id);
            pstmt.setDate(3, sqlDate);
            pstmt.setString(4, bloodGroup);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Your data is saved! Thank you for your cooperation.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void exitApplication() {
        System.exit(0);
    }

    private void navigateBack() {
        new TabsFrame().setVisible(true);
        dispose();
    }

    private void handleBloodGroupChange() {
        String bg = (String) cb_bg.getSelectedItem();
        cb_rh.setEnabled(!bg.equals("BOMBAY BLOOD GROUP"));
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new blood_stock().setVisible(true));
    }

    // UI Components
    private JComboBox<String> cb_bg;
    private JComboBox<String> cb_rh;
    private JButton jButton1, jButton2, jButton3, jButton4;
    private JSpinner t_date;
    private JTextField tf_bagid, tf_did;
}
