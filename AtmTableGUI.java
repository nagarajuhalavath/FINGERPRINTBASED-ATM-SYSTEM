package fingerprint;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AtmTableGUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtAtmId, txtLocation, txtNoOfAtm;
    private JTable tblAtms;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public AtmTableGUI() {
        initializeUI();
        connectToDatabase();
        displayAtms();
    }

    private void initializeUI() {
        txtAtmId = new JTextField();
        txtLocation = new JTextField();
        txtNoOfAtm = new JTextField();

        tblAtms = new JTable();
        tblAtms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAtms.getSelectionModel().addListSelectionListener(e -> selectAtm());

        JScrollPane scrollPane = new JScrollPane(tblAtms);

        btnAdd = new JButton("Add");
        btnModify = new JButton("Modify");
        btnDelete = new JButton("Delete");
        btnDisplay = new JButton("Display");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("ATM ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Location:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("No. of ATMs:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtAtmId, gbc);
        gbc.gridy++;
        panel.add(txtLocation, gbc);
        gbc.gridy++;
        panel.add(txtNoOfAtm, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;

        panel.add(btnAdd, gbc);
        gbc.gridy++;
        panel.add(btnModify, gbc);
        gbc.gridy++;
        panel.add(btnDelete, gbc);
        gbc.gridy++;
        panel.add(btnDisplay, gbc);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> insertAtm());

        btnModify.addActionListener(e -> modifyAtm());

        btnDelete.addActionListener(e -> deleteAtm());

        btnDisplay.addActionListener(e -> displayAtms());

        setTitle("ATM Table App");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void connectToDatabase() {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "nagaraju";
        String password = "nagaraju";

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertAtm() {
        String atmId = txtAtmId.getText();
        String location = txtLocation.getText();
        String noOfAtm = txtNoOfAtm.getText();

        try {
            String query = "INSERT INTO atm (atm_id, location, no_of_atm) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, atmId);
            statement.setString(2, location);
            statement.setString(3, noOfAtm);
            statement.executeUpdate();

            clearFields();
            displayAtms();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyAtm() {
        int selectedRow = tblAtms.getSelectedRow();
        if (selectedRow >= 0) {
            String atmId = txtAtmId.getText();
            String location = txtLocation.getText();
            String noOfAtm = txtNoOfAtm.getText();

            try {
                String query = "UPDATE atm SET location=?, no_of_atm=? WHERE atm_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, location);
                statement.setString(2, noOfAtm);
                statement.setString(3, atmId);
                statement.executeUpdate();

                clearFields();
                displayAtms();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an ATM to modify.");
        }
    }

    private void deleteAtm() {
        int selectedRow = tblAtms.getSelectedRow();
        if (selectedRow >= 0) {
            String atmId = tblAtms.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this ATM?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM atm WHERE atm_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, atmId);
                    statement.executeUpdate();

                    clearFields();
                    displayAtms();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an ATM to delete.");
        }
    }

    private void displayAtms() {
        try {
            String query = "SELECT * FROM atm";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Atm> atms = new ArrayList<>();
            while (resultSet.next()) {
                String atmId = resultSet.getString("atm_id");
                String location = resultSet.getString("location");
                String noOfAtm = resultSet.getString("no_of_atm");
                atms.add(new Atm(atmId, location, noOfAtm));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"ATM ID", "Location", "No. of ATMs"});

            for (Atm atm : atms) {
                model.addRow(new String[]{atm.getAtmId(), atm.getLocation(), atm.getNoOfAtm()});
            }

            tblAtms.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectAtm() {
        int selectedRow = tblAtms.getSelectedRow();
        if (selectedRow >= 0) {
            String atmId = tblAtms.getValueAt(selectedRow, 0).toString();
            String location = tblAtms.getValueAt(selectedRow, 1).toString();
            String noOfAtm = tblAtms.getValueAt(selectedRow, 2).toString();

            txtAtmId.setText(atmId);
            txtLocation.setText(location);
            txtNoOfAtm.setText(noOfAtm);
        }
    }

    private void clearFields() {
        txtAtmId.setText("");
        txtLocation.setText("");
        txtNoOfAtm.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AtmTableGUI::new);
    }

    private class Atm {
        private String atmId;
        private String location;
        private String noOfAtm;

        public Atm(String atmId, String location, String noOfAtm) {
            this.atmId = atmId;
            this.location = location;
            this.noOfAtm = noOfAtm;
        }

        public String getAtmId() {
            return atmId;
        }

        public String getLocation() {
            return location;
        }

        public String getNoOfAtm() {
            return noOfAtm;
        }
    }
}
